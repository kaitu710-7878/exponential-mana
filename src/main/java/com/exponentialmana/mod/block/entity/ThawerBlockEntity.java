package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.ThawerMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModItems;
import com.exponentialmana.mod.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
 * BlockEntity for the Thawer.
 * Features 2x2 (4 slots) input for Batch Compressed Mana, and 10x10 (100 slots) output,
 * each output slot supporting up to Integer.MAX_VALUE stacks.
 * Safely extracts real item-form mana from Batch Compressed Mana into 100 output slots.
 * Supports GUI and preserves contents on block pickup.
 */
public class ThawerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOTS = 4;   // 2x2 grid
    public static final int OUTPUT_SLOTS = 100; // 10x10 grid
    public static final int TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS; // 104

    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        public void setSize(int size) {
            super.setSize(Math.max(TOTAL_SLOTS, size));
        }

        @Override
        public int getSlotLimit(int slot) {
            return (slot < INPUT_SLOTS) ? 64 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < INPUT_SLOTS) {
                return stack.is(ModItems.BATCH_COMPRESSED_MANA.get());
            }
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public ThawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THAWER_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ThawerBlockEntity entity) {
        if (level.isClientSide) return;

        boolean changed = false;

        // Process each input slot
        for (int inSlot = 0; inSlot < INPUT_SLOTS; inSlot++) {
            ItemStack input = entity.itemHandler.getStackInSlot(inSlot);
            if (!input.isEmpty() && input.is(ModItems.BATCH_COMPRESSED_MANA.get())) {
                CompoundTag tag = input.getTag();
                if (tag != null && tag.contains("ManaCounts")) {
                    CompoundTag manaTag = tag.getCompound("ManaCounts");
                    boolean anyRemaining = false;

                    for (int tier = 0; tier <= 10; tier++) {
                        String key = "Tier" + tier;
                        long count = manaTag.getLong(key);
                        if (count > 0) {
                            Item manaItem = getManaItem(tier);
                            // Extract up to 64 per tick per tier to prevent massive single-tick freeze
                            int toExtract = (int) Math.min(count, 64L);
                            int inserted = entity.insertOutputMana(manaItem, toExtract);

                            if (inserted > 0) {
                                count -= inserted;
                                if (count <= 0) {
                                    manaTag.remove(key);
                                } else {
                                    manaTag.putLong(key, count);
                                }
                                changed = true;
                            }

                            if (count > 0) {
                                anyRemaining = true;
                            }
                        }
                    }

                    // If all mana counts are depleted, shrink input stack by 1
                    if (!anyRemaining) {
                        input.shrink(1);
                        entity.itemHandler.setStackInSlot(inSlot, input);
                        changed = true;
                    }
                }
            }
        }

        if (changed) {
            entity.setChanged();
        }
    }

    /**
     * Inserts unpacked mana items into the 10x10 output slots (Int_MAX stack size).
     * Returns the actual amount inserted.
     */
    private int insertOutputMana(Item manaItem, int amount) {
        int remaining = amount;

        // Pass 1: Try adding to existing matching stacks
        for (int i = INPUT_SLOTS; i < TOTAL_SLOTS; i++) {
            ItemStack existing = itemHandler.getStackInSlot(i);
            if (!existing.isEmpty() && existing.is(manaItem)) {
                long space = (long) Integer.MAX_VALUE - existing.getCount();
                if (space > 0) {
                    int toAdd = (int) Math.min((long) remaining, space);
                    existing.grow(toAdd);
                    itemHandler.setStackInSlot(i, existing);
                    remaining -= toAdd;
                    if (remaining <= 0) return amount;
                }
            }
        }

        // Pass 2: Place in empty output slots
        if (remaining > 0) {
            for (int i = INPUT_SLOTS; i < TOTAL_SLOTS; i++) {
                ItemStack existing = itemHandler.getStackInSlot(i);
                if (existing.isEmpty()) {
                    itemHandler.setStackInSlot(i, new ItemStack(manaItem, remaining));
                    return amount;
                }
            }
        }

        return amount - remaining;
    }

    public static Item getManaItem(int tier) {
        return switch (tier) {
            case 0 -> ModItems.TIER0_MANA.get();
            case 1 -> ModItems.TIER1_MANA.get();
            case 2 -> ModItems.TIER2_MANA.get();
            case 3 -> ModItems.TIER3_MANA.get();
            case 4 -> ModItems.TIER4_MANA.get();
            case 5 -> ModItems.TIER5_MANA.get();
            case 6 -> ModItems.TIER6_MANA.get();
            case 7 -> ModItems.TIER7_MANA.get();
            case 8 -> ModItems.TIER8_MANA.get();
            case 9 -> ModItems.TIER9_MANA.get();
            case 10 -> ModItems.TIER10_MANA.get();
            default -> ModItems.TIER0_MANA.get();
        };
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
        return Component.translatable("block.exponential_mana.thawer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ThawerMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        if (itemHandler.getSlots() < TOTAL_SLOTS) {
            itemHandler.setSize(TOTAL_SLOTS);
        }
    }
}
