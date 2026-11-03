package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.ExtendedCraftingMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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
 * BlockEntity for Extended Crafting Tables (Tier 1: 4x4 up to Tier 10: 13x13).
 * Operates without energy and supports automated crafting via AE2 pattern providers and automation pipes.
 * Supports GUI and preserves contents on block pickup.
 */
public class ExtendedCraftingTableBlockEntity extends BlockEntity implements MenuProvider {
    private final int tier;
    private final int gridSize;
    private final int totalInputSlots;
    private final int outputSlotIndex;

    private final ItemStackHandler itemHandler;
    private final LazyOptional<IItemHandler> lazyItemHandler;

    public ExtendedCraftingTableBlockEntity(BlockPos pos, BlockState state, int tier) {
        super(ModBlockEntities.EXTENDED_CRAFTING_TABLE_BE.get(), pos, state);
        this.tier = tier;
        this.gridSize = tier + 3; // Tier 1: 4, Tier 2: 5, ..., Tier 10: 13
        this.totalInputSlots = gridSize * gridSize;
        this.outputSlotIndex = totalInputSlots; // Last slot is output

        this.itemHandler = new ItemStackHandler(totalInputSlots + 1) {
            @Override
            public void setSize(int size) {
                super.setSize(Math.max(totalInputSlots + 1, size));
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                // Output slot cannot have items inserted externally
                return slot < outputSlotIndex;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };

        this.lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    public int getTier() {
        return tier;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getTotalInputSlots() {
        return totalInputSlots;
    }

    public int getOutputSlotIndex() {
        return outputSlotIndex;
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
        return Component.translatable("block.exponential_mana.tier" + tier + "_extended_crafting_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ExtendedCraftingMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("tier", tier);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        if (itemHandler.getSlots() < (totalInputSlots + 1)) {
            itemHandler.setSize(totalInputSlots + 1);
        }
    }
}
