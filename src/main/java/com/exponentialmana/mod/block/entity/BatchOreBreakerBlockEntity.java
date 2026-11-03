package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.block.InactiveManaOreBlock;
import com.exponentialmana.mod.block.ManaOreBlock;
import com.exponentialmana.mod.menu.BatchOreBreakerMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModBlocks;
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
import net.minecraft.world.item.BlockItem;
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

/**
 * BlockEntity for the Batch Ore Breaker.
 * Features:
 * - 10x10 (100 slots) Input Inventory for Inactive & Active Mana Ores (Int_MAX capacity per slot).
 * - 1 Output Slot (Slot 100) that automatically packs accumulated mana into Batch Compressed Mana items.
 * - Lag-free batch processing and safe NBT retention upon block removal.
 */
public class BatchOreBreakerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOTS = 100; // 10x10 grid (slots 0..99)
    public static final int OUTPUT_SLOT = 100;  // Slot 100: Batch Compressed Mana output
    public static final int TOTAL_SLOTS = 101;
    public static final int BATCH_INTERVAL_TICKS = 20; // 1 second

    private int timer = BATCH_INTERVAL_TICKS;

    // Stored mana per tier (0 to 10)
    private final long[] manaPool = new long[11];

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

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public BatchOreBreakerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATCH_ORE_BREAKER_BE.get(), pos, state);
    }

    public static boolean isOreValid(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) {
            Block block = bi.getBlock();
            return block instanceof InactiveManaOreBlock ||
                   block instanceof ManaOreBlock ||
                   block == ModBlocks.INACTIVE_TIER0_MANA_ORE.get();
        }
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BatchOreBreakerBlockEntity entity) {
        if (level.isClientSide) return;

        entity.timer--;
        if (entity.timer <= 0) {
            entity.timer = BATCH_INTERVAL_TICKS;
            entity.processBatch();
        }
    }

    /**
     * Processes inactive and active ores, converting them into mana counts in manaPool,
     * and automatically packs them into the Batch Compressed Mana output slot.
     */
    private void processBatch() {
        boolean changed = false;

        // Process ore inputs
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) {
                Block block = bi.getBlock();
                int tier = -1;
                if (block == ModBlocks.INACTIVE_TIER0_MANA_ORE.get()) {
                    tier = 0;
                } else if (block instanceof InactiveManaOreBlock inactiveBlock) {
                    tier = inactiveBlock.getTier();
                } else if (block instanceof ManaOreBlock manaBlock) {
                    tier = manaBlock.getTier();
                }

                if (tier >= 0 && tier <= 10) {
                    int toExtract = Math.min(stack.getCount(), 64);
                    stack.shrink(toExtract);
                    manaPool[tier] = Math.min((long) Integer.MAX_VALUE * 1000L, manaPool[tier] + toExtract);
                    itemHandler.setStackInSlot(slot, stack);
                    changed = true;
                }
            }
        }

        // Auto-pack accumulated manaPool into output slot as Batch Compressed Mana
        long totalStored = 0;
        for (long c : manaPool) totalStored += c;

        if (totalStored > 0) {
            ItemStack out = itemHandler.getStackInSlot(OUTPUT_SLOT);
            if (out.isEmpty()) {
                ItemStack drop = new ItemStack(ModItems.BATCH_COMPRESSED_MANA.get());
                CompoundTag tag = drop.getOrCreateTag();
                CompoundTag manaTag = new CompoundTag();
                for (int i = 0; i <= 10; i++) {
                    if (manaPool[i] > 0) {
                        manaTag.putLong("Tier" + i, manaPool[i]);
                        manaPool[i] = 0;
                    }
                }
                tag.put("ManaCounts", manaTag);
                itemHandler.setStackInSlot(OUTPUT_SLOT, drop);
                changed = true;
            } else if (out.is(ModItems.BATCH_COMPRESSED_MANA.get())) {
                CompoundTag tag = out.getOrCreateTag();
                CompoundTag manaTag = tag.getCompound("ManaCounts");
                for (int i = 0; i <= 10; i++) {
                    if (manaPool[i] > 0) {
                        long current = manaTag.getLong("Tier" + i);
                        manaTag.putLong("Tier" + i, current + manaPool[i]);
                        manaPool[i] = 0;
                    }
                }
                tag.put("ManaCounts", manaTag);
                changed = true;
            }
        }

        if (changed) {
            setChanged();
        }
    }

    /**
     * Creates the NBT-packed Batch Compressed Mana item when dismantled.
     */
    public ItemStack createDropStack() {
        ItemStack drop = new ItemStack(ModItems.BATCH_COMPRESSED_MANA.get());
        CompoundTag tag = drop.getOrCreateTag();
        CompoundTag manaTag = new CompoundTag();

        // 1. Any remaining manaPool
        for (int tier = 0; tier <= 10; tier++) {
            if (manaPool[tier] > 0) {
                manaTag.putLong("Tier" + tier, manaPool[tier]);
            }
        }

        // 2. Output slot item if present
        ItemStack out = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (!out.isEmpty() && out.hasTag() && out.getTag().contains("ManaCounts")) {
            CompoundTag outMana = out.getTag().getCompound("ManaCounts");
            for (int tier = 0; tier <= 10; tier++) {
                long c = outMana.getLong("Tier" + tier);
                if (c > 0) {
                    long prev = manaTag.getLong("Tier" + tier);
                    manaTag.putLong("Tier" + tier, prev + c);
                }
            }
        }

        // 3. Any unprocessed ores in input slots
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) {
                int oreTier = -1;
                if (bi.getBlock() == ModBlocks.INACTIVE_TIER0_MANA_ORE.get()) oreTier = 0;
                else if (bi.getBlock() instanceof InactiveManaOreBlock inb) oreTier = inb.getTier();
                else if (bi.getBlock() instanceof ManaOreBlock mob) oreTier = mob.getTier();

                if (oreTier >= 0 && oreTier <= 10) {
                    long prev = manaTag.getLong("Tier" + oreTier);
                    manaTag.putLong("Tier" + oreTier, prev + stack.getCount());
                }
            }
        }

        tag.put("ManaCounts", manaTag);
        return drop;
    }

    public long[] getManaPool() {
        return manaPool;
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
        if (tag.contains("ManaPool")) {
            CompoundTag manaTag = tag.getCompound("ManaPool");
            for (int i = 0; i <= 10; i++) {
                manaPool[i] = manaTag.getLong("Tier" + i);
            }
        }
        timer = tag.getInt("timer");
    }
}
