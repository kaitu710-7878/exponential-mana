package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.FairyMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModItems;
import com.exponentialmana.mod.util.side.AutoEjectHelper;
import com.exponentialmana.mod.util.side.ISidedMachine;
import com.exponentialmana.mod.util.side.SideConfig;
import com.exponentialmana.mod.util.side.SidedItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * BlockEntity for Fairy Block and Compressed Fairies (9^1 to 9^10).
 * Features 8x8 (64 slots) Catalyst inputs and 8x8 (64 slots) Mana outputs,
 * each slot supporting up to Integer.MAX_VALUE stacks.
 * Generates tiered mana based on catalyst count, and fills any remaining quota with Tier 0 mana.
 * Total production quota per 60s cycle = 9^compressionTier.
 * Synchronizes cooldown with client via ContainerData for live GUI countdown.
 * Full Mekanism-style Side Configuration and Auto-Eject.
 */
public class FairyBlockEntity extends BlockEntity implements MenuProvider, ISidedMachine {
    public static final int BASE_COOLDOWN = 1200; // 60 seconds at 20 TPS
    public static final int CATALYST_SLOTS = 64; // 8x8
    public static final int OUTPUT_SLOTS = 64;   // 8x8
    public static final int TOTAL_SLOTS = CATALYST_SLOTS + OUTPUT_SLOTS; // 128

    private static final List<Integer> OUTPUT_SLOT_INDICES;
    static {
        List<Integer> list = new ArrayList<>(OUTPUT_SLOTS);
        for (int i = CATALYST_SLOTS; i < TOTAL_SLOTS; i++) {
            list.add(i);
        }
        OUTPUT_SLOT_INDICES = Collections.unmodifiableList(list);
    }

    private int cooldown = BASE_COOLDOWN;
    private final int compressionTier; // 0 for base, 1 to 10 for compressed

    private final SideConfig sideConfig = new SideConfig();

    // ContainerData for real-time cooldown sync with GUI
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? cooldown : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                cooldown = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    // Slots 0..63: Catalysts (Int_MAX limit, not consumed)
    // Slots 64..127: Output Mana (Int_MAX limit)
    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        public void setSize(int size) {
            super.setSize(Math.max(TOTAL_SLOTS, size));
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < CATALYST_SLOTS) {
                return isCatalyst(stack.getItem());
            }
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final Map<Direction, LazyOptional<IItemHandler>> sidedHandlers = new EnumMap<>(Direction.class);
    private final LazyOptional<IItemHandler> allAccessHandler = LazyOptional.of(() -> itemHandler);

    public FairyBlockEntity(BlockPos pos, BlockState state, int compressionTier) {
        super(ModBlockEntities.FAIRY_BLOCK_BE.get(), pos, state);
        this.compressionTier = compressionTier;

        for (Direction dir : Direction.values()) {
            this.sidedHandlers.put(dir, LazyOptional.of(() -> new SidedItemHandler(
                    itemHandler,
                    dir,
                    sideConfig,
                    slot -> slot < CATALYST_SLOTS,              // Input -> catalysts
                    slot -> slot >= CATALYST_SLOTS && slot < TOTAL_SLOTS // Output -> mana
            )));
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }

        entity.cooldown--;
        if (entity.cooldown <= 0) {
            entity.cooldown = BASE_COOLDOWN;
            entity.processProduction();
        }

        // Auto-eject mana to adjacent inventories
        AutoEjectHelper.eject(level, pos, entity.sideConfig, entity.itemHandler, OUTPUT_SLOT_INDICES);
    }

    /**
     * Total cycle quota = 9^compressionTier.
     * Each catalyst in slots 0..63 consumes its count from the quota to produce its corresponding mana tier.
     * Any remaining quota is produced as Tier 0 mana!
     */
    private void processProduction() {
        long totalQuota = 1;
        for (int i = 0; i < compressionTier; i++) {
            totalQuota *= 9L;
        }

        long remainingQuota = totalQuota;

        // Iterate catalysts and allocate quota based on catalyst counts
        for (int i = 0; i < CATALYST_SLOTS; i++) {
            if (remainingQuota <= 0) break;

            ItemStack catalyst = itemHandler.getStackInSlot(i);
            if (!catalyst.isEmpty() && isCatalyst(catalyst.getItem())) {
                Item producedMana = getManaForCatalyst(catalyst.getItem());
                if (producedMana != null) {
                    long toProduce = Math.min((long) catalyst.getCount(), remainingQuota);
                    if (toProduce > 0) {
                        insertOutputMana(producedMana, toProduce);
                        remainingQuota -= toProduce;
                    }
                }
            }
        }

        // Any leftover quota produces Tier 0 Mana
        if (remainingQuota > 0) {
            insertOutputMana(ModItems.TIER0_MANA.get(), remainingQuota);
        }

        setChanged();
    }

    private void insertOutputMana(Item manaItem, long amount) {
        long remaining = amount;

        // Try existing matching stacks first
        for (int i = CATALYST_SLOTS; i < TOTAL_SLOTS && remaining > 0; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(manaItem)) {
                long space = (long) Integer.MAX_VALUE - stack.getCount();
                if (space > 0) {
                    int add = (int) Math.min(space, remaining);
                    stack.grow(add);
                    remaining -= add;
                }
            }
        }

        // Place in empty output slots
        for (int i = CATALYST_SLOTS; i < TOTAL_SLOTS && remaining > 0; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                int add = (int) Math.min((long) Integer.MAX_VALUE, remaining);
                itemHandler.setStackInSlot(i, new ItemStack(manaItem, add));
                remaining -= add;
            }
        }
    }

    public static final java.util.Map<Item, Item> CUSTOM_CATALYSTS = new java.util.concurrent.ConcurrentHashMap<>();

    public static void registerCustomCatalyst(Object catalystObj, Object manaObj) {
        Item catalyst = resolveItem(catalystObj);
        Item mana = resolveItem(manaObj);
        if (catalyst != null && mana != null) {
            CUSTOM_CATALYSTS.put(catalyst, mana);
        }
    }

    private static Item resolveItem(Object obj) {
        if (obj instanceof Item item) return item;
        if (obj instanceof ItemStack stack) return stack.getItem();
        if (obj instanceof java.util.function.Supplier<?> sup && sup.get() instanceof Item item) return item;
        if (obj instanceof String str) {
            net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.tryParse(str);
            if (rl != null && net.minecraftforge.registries.ForgeRegistries.ITEMS.containsKey(rl)) {
                return net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(rl);
            }
        }
        return null;
    }

    public static boolean isCatalyst(Item item) {
        if (item != null && CUSTOM_CATALYSTS.containsKey(item)) return true;
        return item == ModItems.TIER1_FAIRY_CATALYST.get() ||
                item == ModItems.TIER2_FAIRY_CATALYST.get() ||
                item == ModItems.TIER3_FAIRY_CATALYST.get() ||
                item == ModItems.TIER4_FAIRY_CATALYST.get() ||
                item == ModItems.TIER5_FAIRY_CATALYST.get() ||
                item == ModItems.TIER6_FAIRY_CATALYST.get() ||
                item == ModItems.TIER7_FAIRY_CATALYST.get() ||
                item == ModItems.TIER8_FAIRY_CATALYST.get() ||
                item == ModItems.TIER9_FAIRY_CATALYST.get() ||
                item == ModItems.TIER10_FAIRY_CATALYST.get() ||
                item == ModItems.PURE_FAIRY_CATALYST.get();
    }

    public static Item getManaForCatalyst(Item item) {
        if (item != null && CUSTOM_CATALYSTS.containsKey(item)) return CUSTOM_CATALYSTS.get(item);
        if (item == ModItems.TIER1_FAIRY_CATALYST.get()) return ModItems.TIER1_MANA.get();
        if (item == ModItems.TIER2_FAIRY_CATALYST.get()) return ModItems.TIER2_MANA.get();
        if (item == ModItems.TIER3_FAIRY_CATALYST.get()) return ModItems.TIER3_MANA.get();
        if (item == ModItems.TIER4_FAIRY_CATALYST.get()) return ModItems.TIER4_MANA.get();
        if (item == ModItems.TIER5_FAIRY_CATALYST.get()) return ModItems.TIER5_MANA.get();
        if (item == ModItems.TIER6_FAIRY_CATALYST.get()) return ModItems.TIER6_MANA.get();
        if (item == ModItems.TIER7_FAIRY_CATALYST.get()) return ModItems.TIER7_MANA.get();
        if (item == ModItems.TIER8_FAIRY_CATALYST.get()) return ModItems.TIER8_MANA.get();
        if (item == ModItems.TIER9_FAIRY_CATALYST.get()) return ModItems.TIER9_MANA.get();
        if (item == ModItems.TIER10_FAIRY_CATALYST.get()) return ModItems.TIER10_MANA.get();
        if (item == ModItems.PURE_FAIRY_CATALYST.get()) return ModItems.PURE_MANA.get();
        return null;
    }

    public int getCompressionTier() {
        return compressionTier;
    }

    public int getCooldown() {
        return cooldown;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public SideConfig getSideConfig() {
        return sideConfig;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return allAccessHandler.cast();
            }
            if (sideConfig.getMode(side) == com.exponentialmana.mod.util.side.SideMode.DISABLED) {
                return LazyOptional.empty();
            }
            LazyOptional<IItemHandler> sided = sidedHandlers.get(side);
            if (sided != null) {
                return sided.cast();
            }
            return allAccessHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        allAccessHandler.invalidate();
        for (LazyOptional<IItemHandler> sided : sidedHandlers.values()) {
            sided.invalidate();
        }
    }

    public void saveToItem(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
        saveAdditional(tag);
    }

    @Override
    public Component getDisplayName() {
        if (compressionTier == 0) {
            return Component.translatable("block.exponential_mana.fairy_block");
        }
        return Component.translatable("block.exponential_mana.compressed_fairy_block_" + compressionTier);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FairyMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("side_config", sideConfig.serializeNBT());
        tag.putInt("cooldown", cooldown);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        if (itemHandler.getSlots() < TOTAL_SLOTS) {
            itemHandler.setSize(TOTAL_SLOTS);
        }
        if (tag.contains("side_config")) {
            sideConfig.deserializeNBT(tag.getCompound("side_config"));
        }
        if (tag.contains("cooldown")) {
            cooldown = tag.getInt("cooldown");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
