package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.item.BatchCompressedManaItem;
import com.exponentialmana.mod.menu.BatchOreBreakerMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModBlocks;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * BlockEntity for the Batch Ore Breaker.
 * Features:
 * - 10x10 (100 slots) Inventory for Inactive Mana Ores (Int_MAX capacity per slot).
 * - Ores are NOT consumed! Virtual 5-minute cooldown.
 * - Active ores placed in the machine are immediately converted to Inactive Ores ("Batch Inactivation") and produce initial mana.
 * - Output slot (Slot 100) automatically packs all extracted mana into Batch Compressed Mana.
 * - Safely preserves all ores and accumulated mana inside dropped item NBT upon removal.
 * - Full Mekanism-style Side Configuration and Auto-Eject.
 */
public class BatchOreBreakerBlockEntity extends BlockEntity implements MenuProvider, ISidedMachine {
    public static final int INPUT_SLOTS = 100; // 10x10 grid (slots 0..99)
    public static final int OUTPUT_SLOT = 100;  // Slot 100: Batch Compressed Mana output
    public static final int TOTAL_SLOTS = 101;
    public static final int CYCLE_TICKS = 6000; // 5 minutes (300 sec * 20 ticks)

    private int timer = CYCLE_TICKS;
    private final long[] manaPool = new long[12];
    private final SideConfig sideConfig = new SideConfig();

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) return timer;
            if (index == 1) {
                long total = 0;
                for (long c : manaPool) total += c;
                return (int) Math.min((long) Integer.MAX_VALUE, total);
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) timer = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        public void setSize(int size) {
            super.setSize(Math.max(TOTAL_SLOTS, size));
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot < INPUT_SLOTS ? Integer.MAX_VALUE : 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < INPUT_SLOTS) {
                return isOreValid(stack);
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

    public BatchOreBreakerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATCH_ORE_BREAKER_BE.get(), pos, state);

        for (Direction dir : Direction.values()) {
            this.sidedHandlers.put(dir, LazyOptional.of(() -> new SidedItemHandler(
                    itemHandler,
                    dir,
                    sideConfig,
                    slot -> slot < INPUT_SLOTS,     // Input -> ores
                    slot -> slot == OUTPUT_SLOT     // Output -> compressed mana
            )));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BatchOreBreakerBlockEntity entity) {
        // 1. Process instant batch inactivation of active ores inserted into machine
        entity.processBatchInactivation();

        // 2. Decrement 5-minute virtual mining timer
        entity.timer--;
        if (entity.timer <= 0) {
            entity.timer = CYCLE_TICKS;
            entity.processVirtualMiningCycle();
        }

        // 3. Auto-pack accumulated manaPool into output slot
        entity.packManaToOutputSlot();

        // 4. Auto-eject compressed mana to adjacent inventories
        AutoEjectHelper.eject(level, pos, entity.sideConfig, entity.itemHandler, Collections.singletonList(OUTPUT_SLOT));
    }

    /**
     * Instantly converts any Active Mana Ores in slots into their Inactive counterparts
     * and grants their initial yield into the mana pool.
     */
    private void processBatchInactivation() {
        boolean changed = false;
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int activeTier = getActiveOreTier(stack);
                if (activeTier >= 0) {
                    int count = stack.getCount();
                    manaPool[activeTier] += count;
                    ItemStack inactiveStack = getInactiveStackForActive(stack.getItem(), activeTier, count);
                    if (!inactiveStack.isEmpty()) {
                        itemHandler.setStackInSlot(i, inactiveStack);
                    }
                    changed = true;
                }
            }
        }
        if (changed) {
            setChanged();
        }
    }

    /**
     * Non-destructive virtual mining: All inactive ores in the 10x10 inventory generate
     * their corresponding tiered mana. Ores remain in the inventory.
     */
    private void processVirtualMiningCycle() {
        boolean any = false;
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int inactiveTier = getInactiveOreTier(stack);
                if (inactiveTier >= 0) {
                    manaPool[inactiveTier] += stack.getCount();
                    any = true;
                }
            }
        }
        if (any) {
            setChanged();
        }
    }

    /**
     * Packs accumulated mana from manaPool into slot 100 as BATCH_COMPRESSED_MANA.
     */
    private void packManaToOutputSlot() {
        long totalPending = 0;
        for (long c : manaPool) totalPending += c;
        if (totalPending <= 0) return;

        ItemStack existing = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) {
            ItemStack newStack = new ItemStack(ModItems.BATCH_COMPRESSED_MANA.get(), 1);
            for (int t = 0; t <= 11; t++) {
                if (manaPool[t] > 0) {
                    BatchCompressedManaItem.addMana(newStack, t, manaPool[t]);
                    manaPool[t] = 0;
                }
            }
            itemHandler.setStackInSlot(OUTPUT_SLOT, newStack);
            setChanged();
        } else if (existing.is(ModItems.BATCH_COMPRESSED_MANA.get())) {
            for (int t = 0; t <= 11; t++) {
                if (manaPool[t] > 0) {
                    BatchCompressedManaItem.addMana(existing, t, manaPool[t]);
                    manaPool[t] = 0;
                }
            }
            setChanged();
        }
    }

    public static boolean isOreValid(ItemStack stack) {
        return getActiveOreTier(stack) >= 0 || getInactiveOreTier(stack) >= 0;
    }

    public record CustomOreEntry(Item activeOre, Item inactiveOre, int tier) {}
    public static final java.util.List<CustomOreEntry> CUSTOM_ORES = new java.util.concurrent.CopyOnWriteArrayList<>();

    public static void registerCustomOre(Object activeObj, Object inactiveObj, int tier) {
        Item active = resolveItem(activeObj);
        Item inactive = resolveItem(inactiveObj);
        if (active != null && inactive != null && tier >= 0 && tier <= 11) {
            CUSTOM_ORES.add(new CustomOreEntry(active, inactive, tier));
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

    public static int getActiveOreTier(ItemStack stack) {
        Item item = stack.getItem();
        for (CustomOreEntry entry : CUSTOM_ORES) {
            if (item == entry.activeOre()) return entry.tier();
        }
        if (item == ModBlocks.TIER0_MANA_ORE.get().asItem()) return 0;
        if (item == ModBlocks.TIER1_MANA_ORE.get().asItem()) return 1;
        if (item == ModBlocks.TIER2_MANA_ORE.get().asItem()) return 2;
        if (item == ModBlocks.TIER3_MANA_ORE.get().asItem()) return 3;
        if (item == ModBlocks.TIER4_MANA_ORE.get().asItem()) return 4;
        if (item == ModBlocks.TIER5_MANA_ORE.get().asItem()) return 5;
        if (item == ModBlocks.TIER6_MANA_ORE.get().asItem()) return 6;
        if (item == ModBlocks.TIER7_MANA_ORE.get().asItem()) return 7;
        if (item == ModBlocks.TIER8_MANA_ORE.get().asItem()) return 8;
        if (item == ModBlocks.TIER9_MANA_ORE.get().asItem()) return 9;
        if (item == ModBlocks.TIER10_MANA_ORE.get().asItem()) return 10;
        if (item == ModBlocks.PURE_MANA_ORE.get().asItem()) return 11;
        return -1;
    }

    public static int getInactiveOreTier(ItemStack stack) {
        Item item = stack.getItem();
        for (CustomOreEntry entry : CUSTOM_ORES) {
            if (item == entry.inactiveOre()) return entry.tier();
        }
        if (item == ModBlocks.INACTIVE_TIER0_MANA_ORE.get().asItem()) return 0;
        if (item == ModBlocks.INACTIVE_TIER1_MANA_ORE.get().asItem()) return 1;
        if (item == ModBlocks.INACTIVE_TIER2_MANA_ORE.get().asItem()) return 2;
        if (item == ModBlocks.INACTIVE_TIER3_MANA_ORE.get().asItem()) return 3;
        if (item == ModBlocks.INACTIVE_TIER4_MANA_ORE.get().asItem()) return 4;
        if (item == ModBlocks.INACTIVE_TIER5_MANA_ORE.get().asItem()) return 5;
        if (item == ModBlocks.INACTIVE_TIER6_MANA_ORE.get().asItem()) return 6;
        if (item == ModBlocks.INACTIVE_TIER7_MANA_ORE.get().asItem()) return 7;
        if (item == ModBlocks.INACTIVE_TIER8_MANA_ORE.get().asItem()) return 8;
        if (item == ModBlocks.INACTIVE_TIER9_MANA_ORE.get().asItem()) return 9;
        if (item == ModBlocks.INACTIVE_TIER10_MANA_ORE.get().asItem()) return 10;
        if (item == ModBlocks.INACTIVE_PURE_MANA_ORE.get().asItem()) return 11;
        return -1;
    }

    private Block getInactiveBlockForTier(int tier) {
        return switch (tier) {
            case 0 -> ModBlocks.INACTIVE_TIER0_MANA_ORE.get();
            case 1 -> ModBlocks.INACTIVE_TIER1_MANA_ORE.get();
            case 2 -> ModBlocks.INACTIVE_TIER2_MANA_ORE.get();
            case 3 -> ModBlocks.INACTIVE_TIER3_MANA_ORE.get();
            case 4 -> ModBlocks.INACTIVE_TIER4_MANA_ORE.get();
            case 5 -> ModBlocks.INACTIVE_TIER5_MANA_ORE.get();
            case 6 -> ModBlocks.INACTIVE_TIER6_MANA_ORE.get();
            case 7 -> ModBlocks.INACTIVE_TIER7_MANA_ORE.get();
            case 8 -> ModBlocks.INACTIVE_TIER8_MANA_ORE.get();
            case 9 -> ModBlocks.INACTIVE_TIER9_MANA_ORE.get();
            case 10 -> ModBlocks.INACTIVE_TIER10_MANA_ORE.get();
            case 11 -> ModBlocks.INACTIVE_PURE_MANA_ORE.get();
            default -> null;
        };
    }

    private ItemStack getInactiveStackForActive(Item activeItem, int tier, int count) {
        for (CustomOreEntry entry : CUSTOM_ORES) {
            if (activeItem == entry.activeOre()) {
                return new ItemStack(entry.inactiveOre(), count);
            }
        }
        Block inactiveBlock = getInactiveBlockForTier(tier);
        if (inactiveBlock != null) {
            return new ItemStack(inactiveBlock, count);
        }
        return ItemStack.EMPTY;
    }

    public int getTimer() {
        return timer;
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
        return Component.translatable("block.exponential_mana.batch_ore_breaker");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BatchOreBreakerMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("side_config", sideConfig.serializeNBT());
        CompoundTag manaTag = new CompoundTag();
        for (int i = 0; i <= 10; i++) {
            manaTag.putLong("Tier" + i, manaPool[i]);
        }
        tag.put("ManaPool", manaTag);
        tag.putInt("timer", timer);
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
        if (tag.contains("ManaPool")) {
            CompoundTag manaTag = tag.getCompound("ManaPool");
            for (int i = 0; i <= 10; i++) {
                manaPool[i] = manaTag.getLong("Tier" + i);
            }
        }
        timer = tag.getInt("timer");
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
