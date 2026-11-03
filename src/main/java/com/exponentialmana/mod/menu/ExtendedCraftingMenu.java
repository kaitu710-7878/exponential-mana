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
    private final int bufferSlotStart;
    private final int bufferSlotsCount = ExtendedCraftingTableBlockEntity.BUFFER_SLOTS;
    private final int totalMachineSlots;

    private final int bufferStartX;
    private final int bufferStartY;
    private final int playerInvX;
    private final int playerInvY;
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
        this.bufferSlotStart = blockEntity.getBufferSlotStart();
        this.totalMachineSlots = blockEntity.getTotalSlots();

        int gridStartX = 10;
        int gridStartY = 18;
        int gridWidth = gridSize * 18;
        int gridHeight = gridSize * 18;

        this.imageWidth = Math.max(184, gridWidth + 86);

        // 1. Input matrix slots (18x18 pitch always, matching Extended Crafting Reforked)
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int slotIndex = col + row * gridSize;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        gridStartX + col * 18, gridStartY + row * 18));
            }
        }

        // 2. Output Slot (to the right of the grid, centered vertically)
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
                blockEntity.onManualTake();
                super.onTake(player, stack);
            }
        });

        // 3. Material Buffer (18 slots: 2 rows of 9) - Mekanism Formulaic Assemblicator stock
        this.bufferStartX = (imageWidth - 162) / 2;
        this.bufferStartY = gridStartY + gridHeight + 12;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                int bIndex = bufferSlotStart + col + row * 9;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), bIndex,
                        bufferStartX + col * 18, bufferStartY + row * 18));
            }
        }

        // 4. Player Inventory (centered horizontally)
        this.playerInvX = (imageWidth - 162) / 2;
        this.playerInvY = bufferStartY + 2 * 18 + 12;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        // 5. Player Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, playerInvX + col * 18, playerInvY + 58));
        }

        this.imageHeight = playerInvY + 86;
    }

    public ExtendedCraftingTableBlockEntity getBlockEntity() {
        return blockEntity;
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

    public int getBufferSlotStart() {
        return bufferSlotStart;
    }

    public int getBufferStartX() {
        return bufferStartX;
    }

    public int getBufferStartY() {
        return bufferStartY;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, blockEntity.getBlockState().getBlock());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            int totalMachine = totalMachineSlots; // grid + output + buffer
            int playerInvStart = totalMachine;
            int playerInvEnd = playerInvStart + 36;

            if (index == outputSlotIndex) {
                // Taking from output slot
                if (!this.moveItemStackTo(slotStack, playerInvStart, playerInvEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemstack);
                blockEntity.onManualTake();
            } else if (index < totalMachine) {
                // Machine slots -> Player Inventory
                if (!this.moveItemStackTo(slotStack, playerInvStart, playerInvEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Player inventory -> Material buffer first, else grid
                int bufferStartInMenu = totalInputSlots + 1; // Slot index in menu for buffer
                int bufferEndInMenu = bufferStartInMenu + bufferSlotsCount;
                if (!this.moveItemStackTo(slotStack, bufferStartInMenu, bufferEndInMenu, false)) {
                    // Try grid slots
                    if (!this.moveItemStackTo(slotStack, 0, totalInputSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return itemstack;
    }
}
