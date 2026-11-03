package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.FairyMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

/**
 * BlockEntity for Fairy Blocks and Compressed Fairy Blocks.
 * Features 8x8 (64 slots) Catalyst inputs and 8x8 (64 slots) Mana outputs,
 * each slot supporting up to Integer.MAX_VALUE stacks.
 * Generates tiered mana based on catalyst count, and fills any remaining quota with Tier 0 mana.
 * Total production quota per 60s cycle = 9^compressionTier.
 * Synchronizes cooldown with client via ContainerData for live GUI countdown.
 */
public class FairyBlockEntity extends BlockEntity implements MenuProvider {
    public static final int BASE_COOLDOWN = 1200; // 60 seconds at 20 TPS
    public static final int CATALYST_SLOTS = 64; // 8x8
    public static final int OUTPUT_SLOTS = 64;   // 8x8
    public static final int TOTAL_SLOTS = CATALYST_SLOTS + OUTPUT_SLOTS; // 128

    private int cooldown = BASE_COOLDOWN;
    private final int compressionTier; // 0 for base, 1 to 10 for compressed

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

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public FairyBlockEntity(BlockPos pos, BlockState state, int compressionTier) {
        super(ModBlockEntities.FAIRY_BLOCK_BE.get(), pos, state);
        this.compressionTier = compressionTier;
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

        // Remainder of quota is produced as Tier 0 mana!
        if (remainingQuota > 0) {
            insertOutputMana(ModItems.TIER0_MANA.get(), remainingQuota);
        }
    }

    private void insertOutputMana(Item manaItem, long amount) {
        long remaining = amount;

        // Pass 1: Try adding to existing matching stacks in output slots
        for (int i = CATALYST_SLOTS; i < TOTAL_SLOTS; i++) {
            ItemStack existing = itemHandler.getStackInSlot(i);
            if (!existing.isEmpty() && existing.is(manaItem)) {
                long space = (long) Integer.MAX_VALUE - existing.getCount();
                if (space > 0) {
                    long toAdd = Math.min(remaining, space);
                    existing.grow((int) toAdd);
                    itemHandler.setStackInSlot(i, existing);
                    remaining -= toAdd;
                    if (remaining <= 0) break;
                }
            }
        }

        // Pass 2: Place in empty output slots
        if (remaining > 0) {
            for (int i = CATALYST_SLOTS; i < TOTAL_SLOTS; i++) {
                ItemStack existing = itemHandler.getStackInSlot(i);
                if (existing.isEmpty()) {
                    long toAdd = Math.min(remaining, (long) Integer.MAX_VALUE);
                    itemHandler.setStackInSlot(i, new ItemStack(manaItem, (int) toAdd));
                    remaining -= toAdd;
                    if (remaining <= 0) break;
                }
            }
        }
    }

    public static boolean isCatalyst(Item item) {
        return getManaForCatalyst(item) != null;
    }

    @Nullable
    public static Item getManaForCatalyst(Item item) {
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
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
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
        if (tag.contains("cooldown")) {
            cooldown = tag.getInt("cooldown");
        }
    }
}
