package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.ThawerMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModItems;
import com.exponentialmana.mod.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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
 * Safely extracts real item-form mana from Batch Compressed Mana into output slots.
 * Supports GUI and preserves contents on block pickup.
 */
public class ThawerBlockEntity extends BlockEntity implements MenuProvider {
    // Slot 0: Input Batch Compressed Mana
    // Slot 1 to 11: Output Mana for Tier 0 to 10
    public static final int TOTAL_SLOTS = 12;

    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return (slot == 0) ? 1 : 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
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

        ItemStack input = entity.itemHandler.getStackInSlot(0);
        if (!input.isEmpty() && input.is(ModItems.BATCH_COMPRESSED_MANA.get())) {
            CompoundTag tag = input.getTag();
            if (tag != null && tag.contains("ManaCounts")) {
                CompoundTag manaTag = tag.getCompound("ManaCounts");
                boolean anyRemaining = false;

                for (int tier = 0; tier <= 10; tier++) {
                    String key = "Tier" + tier;
                    long count = manaTag.getLong(key);
                    if (count > 0) {
                        int outputSlot = tier + 1;
                        Item manaItem = getManaItem(tier);
                        ItemStack currentOutput = entity.itemHandler.getStackInSlot(outputSlot);

                        if (currentOutput.isEmpty() || (currentOutput.is(manaItem) && currentOutput.getCount() < 64)) {
                            int space = currentOutput.isEmpty() ? 64 : (64 - currentOutput.getCount());
                            int toExtract = (int) Math.min(count, (long) space);

                            if (toExtract > 0) {
                                count -= toExtract;
                                if (count <= 0) {
                                    manaTag.remove(key);
                                } else {
                                    manaTag.putLong(key, count);
                                }

                                if (currentOutput.isEmpty()) {
                                    entity.itemHandler.setStackInSlot(outputSlot, new ItemStack(manaItem, toExtract));
                                } else {
                                    currentOutput.grow(toExtract);
                                    entity.itemHandler.setStackInSlot(outputSlot, currentOutput);
                                }
                                entity.setChanged();
                            }
                        }

                        if (count > 0) {
                            anyRemaining = true;
                        }
                    }
                }

                // If completely thawed and all tiers empty, consume the input container
                if (!anyRemaining) {
                    input.shrink(1);
                    entity.itemHandler.setStackInSlot(0, input);
                    entity.setChanged();
                }
            }
        }
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

    public boolean isThawing() {
        return !itemHandler.getStackInSlot(0).isEmpty();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        if (level != null) {
            Containers.dropContents(level, worldPosition, inventory);
        }
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
    }
}
