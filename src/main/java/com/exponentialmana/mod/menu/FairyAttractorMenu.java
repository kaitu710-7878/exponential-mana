package com.exponentialmana.mod.menu;

import com.exponentialmana.mod.block.entity.FairyAttractorBlockEntity;
import com.exponentialmana.mod.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FairyAttractorMenu extends AbstractContainerMenu {
    public static final int SLOTS = 64; // 8x8 slots

    private final FairyAttractorBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;

    public FairyAttractorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3));
    }

    public FairyAttractorMenu(int containerId, Inventory inv, BlockEntity entity) {
        this(containerId, inv, entity, ((FairyAttractorBlockEntity) entity).getDataAccess());
    }

    public FairyAttractorMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.FAIRY_ATTRACTOR_MENU.get(), containerId);
        this.blockEntity = (FairyAttractorBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        addDataSlots(data);

        // 8x8 output slots for attracted fairies (0 .. 63)
        int startX = 8;
        int startY = 18;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int slotIndex = col + row * 8;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        startX + col * 18, startY + row * 18) {
                    @Override
                    public int getMaxStackSize() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public int getMaxStackSize(@NotNull ItemStack stack) {
                        return Integer.MAX_VALUE;
                    }
                });
            }
        }

        // Player Inventory (3 rows of 9)
        int playerInvX = 8;
        int playerInvY = startY + 8 * 18 + 14;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        // Player Hotbar (1 row of 9)
        int hotbarY = playerInvY + 3 * 18 + 4;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, playerInvX + col * 18, hotbarY));
        }
    }

    public FairyAttractorBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getTimer() {
        return data.get(0);
    }

    public int getMaxTimer() {
        return data.get(1);
    }

    public int getCompressionTier() {
        return data.get(2);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < SLOTS) {
                // Moving from attractor to player
                if (!this.moveItemStackTo(stackInSlot, SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to attractor
                if (!this.moveItemStackTo(stackInSlot, 0, SLOTS, false)) {
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
