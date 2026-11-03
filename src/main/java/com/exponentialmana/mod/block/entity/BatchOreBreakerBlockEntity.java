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
 * - 10x10 (100 slots) Inventory for Inactive Mana Ores (Int_MAX capacity per slot).
 * - Ores are NOT consumed! The machine manages the 5-minute (6000 ticks) inactive-to-active cooldown virtually.
 * - Active ores placed in the machine are immediately converted to Inactive Ores ("Batch Inactivation") and produce mana.
 * - Every 5 minutes (300 seconds), all stored inactive ores are virtually mined/broken, generating their corresponding
 *   tiered mana without consuming the ores, then resetting to the inactive cooldown.
 * - Output slot (Slot 100) automatically packs all extracted mana into Batch Compressed Mana.
 * - Safely preserves all ores and accumulated mana inside dropped item NBT upon removal.
 */
public class BatchOreBreakerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOTS = 100; // 10x10 grid (slots 0..99)
    public static final int OUTPUT_SLOT = 100;  // Slot 100: Batch Compressed Mana output
    public static final int TOTAL_SLOTS = 101;
    public static final int CYCLE_TICKS = 6000; // 5 minutes (300 sec * 20 ticks)

    private int timer = CYCLE_TICKS;

    // Stored mana per tier (0 to 10) waiting to be packed into output slot
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

        // 1. Instant Batch Inactivation for active ores inserted into the machine
        entity.processActiveOresInactivation();

        // 2. Countdown virtual 5-minute cycle
        entity.timer--;
        if (entity.timer <= 0) {
            entity.timer = CYCLE_TICKS;
            entity.processVirtualMiningCycle();
        }

        // 3. Keep output slot packed with Batch Compressed Mana
        entity.updateOutputSlot();
    }

    /**
     * If player inserts active ManaOreBlocks into the machine, immediately inactivate them
     * into InactiveManaOreBlocks and grant the initial mining mana yield (Batch Inactivation).
     */
    private void processActiveOresInactivation() {
        boolean changed = false;
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) {
                Block block = bi.getBlock();
                if (block instanceof ManaOreBlock manaBlock) {
                    int tier = manaBlock.getTier();
                    Block inactiveBlock = getInactiveBlockForTier(tier);
                    if (inactiveBlock != null) {
                        int count = stack.getCount();
                        // Grant mining yield
                        manaPool[tier] = Math.min((long) Integer.MAX_VALUE * 1000L, manaPool[tier] + count);
                        // Convert stack in-place to Inactive Ore
                        itemHandler.setStackInSlot(slot, new ItemStack(inactiveBlock.asItem(), count));
                        changed = true;
                    }
                }
            }
        }
        if (changed) setChanged();
    }

    /**
     * Executes the virtual 5-minute mining cycle:
     * All inactive ores in the 10x10 inventory produce their corresponding mana WITHOUT being consumed!
     */
    private void processVirtualMiningCycle() {
        boolean changed = false;

        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) {
                Block block = bi.getBlock();
                int tier = -1;
                if (block == ModBlocks.INACTIVE_TIER0_MANA_ORE.get()) {
                    tier = 0;
                } else if (block instanceof InactiveManaOreBlock inactiveBlock) {
                    tier = inactiveBlock.getTier();
                }

                if (tier >= 0 && tier <= 10) {
                    // Extract mana without shrinking the ore!
                    long amount = stack.getCount();
                    manaPool[tier] = Math.min((long) Integer.MAX_VALUE * 1000L, manaPool[tier] + amount);
                    changed = true;
                }
            }
        }

        if (changed) {
            setChanged();
        }
    }

    /**
     * Packs accumulated mana from manaPool into the Output Slot (Slot 100) as Batch Compressed Mana.
     */
    private void updateOutputSlot() {
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
                setChanged();
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
                setChanged();
            }
        }
    }

    @Nullable
    private static Block getInactiveBlockForTier(int tier) {
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
            default -> null;
        };
    }

    /**
     * Creates the NBT-packed Batch Compressed Mana item when dismantled.
     */
    public ItemStack createDropStack() {
        ItemStack drop = new ItemStack(ModItems.BATCH_COMPRESSED_MANA.get());
        CompoundTag tag = drop.getOrCreateTag();
        CompoundTag manaTag = new CompoundTag();

        for (int tier = 0; tier <= 10; tier++) {
            if (manaPool[tier] > 0) {
                manaTag.putLong("Tier" + tier, manaPool[tier]);
            }
        }

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

        tag.put("ManaCounts", manaTag);
        return drop;
    }

    public long[] getManaPool() {
        return manaPool;
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
