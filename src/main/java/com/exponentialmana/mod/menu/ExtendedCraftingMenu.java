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

        // Calculate slot layout coordinates
        // Grid slots on the left / center
        int startX = 8;
        int startY = 18;

        // If grid is large (> 9), scale slot spacing to fit screen
        int slotSize = (gridSize <= 7) ? 18 : (gridSize <= 9) ? 16 : 14;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int slotIndex = col + row * gridSize;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        startX + col * slotSize, startY + row * slotSize));
            }
        }

        // Output Slot (to the right of the grid)
        int outputX = startX + gridSize * slotSize + 20;
        int outputY = startY + (gridSize * slotSize) / 2 - 8;
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), outputSlotIndex, outputX, outputY) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                // Consume 1 item from each non-empty input slot
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

        // Player Inventory
        int playerInvY = Math.max(startY + gridSize * slotSize + 14, 140);
        int playerInvX = Math.max(startX, (startX + gridSize * slotSize + 40 - 162) / 2);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        // Player Hotbar
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

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int totalTableSlots = totalInputSlots + 1; // inputs + 1 output

            if (index == outputSlotIndex) {
                // Taking from output
                if (!this.moveItemStackTo(stackInSlot, totalTableSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index < totalTableSlots) {
                // From input grid to player
                if (!this.moveItemStackTo(stackInSlot, totalTableSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player to input grid
                if (!this.moveItemStackTo(stackInSlot, 0, totalInputSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, blockEntity.getBlockState().getBlock());
    }
}
