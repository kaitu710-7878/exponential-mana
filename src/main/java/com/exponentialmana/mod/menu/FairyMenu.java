package com.exponentialmana.mod.menu;

import com.exponentialmana.mod.block.entity.FairyBlockEntity;
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

public class FairyMenu extends AbstractContainerMenu {
    public static final int CATALYST_SLOTS = FairyBlockEntity.CATALYST_SLOTS; // 64
    public static final int OUTPUT_SLOTS = FairyBlockEntity.OUTPUT_SLOTS;     // 64
    public static final int TOTAL_MACHINE_SLOTS = FairyBlockEntity.TOTAL_SLOTS; // 128

    private final FairyBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public FairyMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FairyMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.FAIRY_MENU.get(), containerId);
        this.blockEntity = (FairyBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // Left: 8x8 Catalyst Inputs (0..63)
        int catStartX = 12;
        int catStartY = 20;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int slotIndex = col + row * 8;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        catStartX + col * 18, catStartY + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return FairyBlockEntity.isCatalyst(stack.getItem());
                    }
                });
            }
        }

        // Right: 8x8 Mana Outputs (64..127)
        int outStartX = 184;
        int outStartY = 20;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int slotIndex = CATALYST_SLOTS + col + row * 8;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        outStartX + col * 18, outStartY + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false; // Player cannot insert items into output slots directly
                    }
                });
            }
        }

        // Player Inventory (3 rows x 9 columns) centered
        int playerInvX = 89;
        int playerInvY = 176;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        // Player Hotbar (1 row x 9 columns)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, playerInvX + col * 18, playerInvY + 58));
        }
    }

    public FairyBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < TOTAL_MACHINE_SLOTS) {
                // Moving from machine (catalyst or output) to player inventory
                if (!this.moveItemStackTo(stackInSlot, TOTAL_MACHINE_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to machine
                if (FairyBlockEntity.isCatalyst(stackInSlot.getItem())) {
                    if (!this.moveItemStackTo(stackInSlot, 0, CATALYST_SLOTS, false)) {
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
