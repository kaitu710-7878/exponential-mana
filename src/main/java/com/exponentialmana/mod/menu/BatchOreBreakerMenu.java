package com.exponentialmana.mod.menu;

import com.exponentialmana.mod.block.entity.BatchOreBreakerBlockEntity;
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

public class BatchOreBreakerMenu extends AbstractContainerMenu {
    public static final int MACHINE_SLOTS = 100;
    private final BatchOreBreakerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public BatchOreBreakerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BatchOreBreakerMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.BATCH_ORE_BREAKER_MENU.get(), containerId);
        this.blockEntity = (BatchOreBreakerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // 10x10 Grid (100 Slots) for Inactive & Active Ores
        // Width of grid: 10 * 18 = 180. Center in panel (panel width = 198)
        // startX = 9, startY = 18
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int slotIndex = col + row * 10;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex, 10 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return BatchOreBreakerBlockEntity.isOreValid(stack);
                    }
                });
            }
        }

        // Player Inventory (3 rows x 9 columns)
        // startX = (198 - 162) / 2 = 18
        int playerInvY = 206;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 18 + col * 18, playerInvY + row * 18));
            }
        }

        // Player Hotbar (1 row x 9 columns)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 18 + col * 18, playerInvY + 58));
        }
    }

    public BatchOreBreakerBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < MACHINE_SLOTS) {
                // Moving from machine to player inventory
                if (!this.moveItemStackTo(stackInSlot, MACHINE_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to machine
                if (BatchOreBreakerBlockEntity.isOreValid(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, MACHINE_SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
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
