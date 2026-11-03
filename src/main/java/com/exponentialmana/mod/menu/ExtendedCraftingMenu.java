package com.exponentialmana.mod.menu;

import com.exponentialmana.mod.block.entity.ExtendedCraftingTableBlockEntity;
import com.exponentialmana.mod.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ExtendedCraftingMenu extends AbstractContainerMenu {
    private final ExtendedCraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final int gridSize;
    private final int totalInputSlots;
    private final int outputSlotIndex;
    private final int imageWidth;
    private final int imageHeight;

    public ExtendedCraftingMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ExtendedCraftingMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.EXTENDED_CRAFTING_MENU.get(), containerId);
        this.blockEntity = (ExtendedCraftingTableBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.gridSize = blockEntity.getGridSize();
        this.totalInputSlots = blockEntity.getTotalInputSlots();
        this.outputSlotIndex = blockEntity.getOutputSlotIndex();

        int gridStartX = 10;
        int gridStartY = 18;
        int gridWidth = gridSize * 18;
        int gridHeight = gridSize * 18;

        this.imageWidth = Math.max(184, gridWidth + 80);
        int playerInvY = gridStartY + gridHeight + 12;
        this.imageHeight = playerInvY + 86;

        // 1. Input matrix slots (18x18 pitch always, matching Extended Crafting)
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int slotIndex = col + row * gridSize;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        gridStartX + col * 18, gridStartY + row * 18));
            }
        }

        // 2. Output Slot (to the right of the grid, centered vertically)
        // 26x26 big frame has item at +5, +5
        int outputFrameX = gridStartX + gridWidth + 34;
        int outputFrameY = gridStartY + gridHeight / 2 - 13;
        int outputItemX = outputFrameX + 5;
        int outputItemY = outputFrameY + 5;

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), outputSlotIndex, outputItemX, outputItemY) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                for (int i = 0; i < totalInputSlots; i++) {
                    ItemStack in = blockEntity.getItemHandler().getStackInSlot(i);
                    if (!in.isEmpty()) {
                        blockEntity.getItemHandler().extractItem(i, 1, false);
                    }
                }
                blockEntity.setChanged();
                super.onTake(player, stack);
            }
        });

        // 3. Player Inventory (centered horizontally)
        int playerInvX = (imageWidth - 162) / 2;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        // 4. Player Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, playerInvX + col * 18, playerInvY + 58));
        }
    }

    public ExtendedCraftingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getOutputSlotIndex() {
        return outputSlotIndex;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int totalTableSlots = totalInputSlots + 1; // inputs + output

            if (index == outputSlotIndex) {
                // Taking from output to player inventory
                if (!this.moveItemStackTo(stackInSlot, totalTableSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index < totalInputSlots) {
                // Taking from input matrix to player inventory
                if (!this.moveItemStackTo(stackInSlot, totalTableSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to input matrix
                if (!this.moveItemStackTo(stackInSlot, 0, totalInputSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, blockEntity.getBlockState().getBlock());
    }
}
