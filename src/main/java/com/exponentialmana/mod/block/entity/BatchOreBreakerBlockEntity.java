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
 * Features a 10x10 (100 slots) inventory, each slot holding up to Int_MAX.
 * Processes both inactive and active mana ores in batches into internal mana pools.
 * Supports GUI and preserves contents on block pickup.
 */
public class BatchOreBreakerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOTS = 100; // 10x10 grid
    public static final int BATCH_INTERVAL_TICKS = 20; // Process every 1 second
    private int timer = BATCH_INTERVAL_TICKS;

    // Stored mana per tier (0 to 10)
    private final long[] manaPool = new long[11];

    // 10x10 item slots with Int_MAX limit
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isOreValid(stack);
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
     * Processes inactive and active ores in the 10x10 inventory, converting them into mana counts.
     */
    private void processBatch() {
        boolean changed = false;

        for (int slot = 0; slot < SLOTS; slot++) {
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
                    // Extract up to 64 per batch (or all remaining if smaller)
                    int toExtract = Math.min(stack.getCount(), 64);
                    stack.shrink(toExtract);
                    manaPool[tier] = Math.min((long) Integer.MAX_VALUE * 1000L, manaPool[tier] + toExtract);
                    itemHandler.setStackInSlot(slot, stack);
                    changed = true;
                }
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

        for (int tier = 0; tier <= 10; tier++) {
            long totalTierMana = manaPool[tier];
            // Also convert any remaining unprocessed inactive/active ores in slots to mana count
            for (int slot = 0; slot < SLOTS; slot++) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) {
                    int oreTier = -1;
                    if (bi.getBlock() == ModBlocks.INACTIVE_TIER0_MANA_ORE.get()) oreTier = 0;
                    else if (bi.getBlock() instanceof InactiveManaOreBlock inb) oreTier = inb.getTier();
                    else if (bi.getBlock() instanceof ManaOreBlock mob) oreTier = mob.getTier();

                    if (oreTier == tier) {
                        totalTierMana += stack.getCount();
                    }
                }
            }
            if (totalTierMana > 0) {
                manaTag.putLong("Tier" + tier, totalTierMana);
            }
        }

        tag.put("ManaCounts", manaTag);
        return drop;
    }

    public long[] getManaPool() {
        return manaPool;
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
        return new BatchOreBreakerMenu(containerId, playerInventory, this);
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
        if (tag.contains("ManaPool")) {
            CompoundTag manaTag = tag.getCompound("ManaPool");
            for (int i = 0; i <= 10; i++) {
                manaPool[i] = manaTag.getLong("Tier" + i);
            }
        }
        timer = tag.getInt("timer");
    }
}
