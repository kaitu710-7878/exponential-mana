package com.exponentialmana.mod.menu;

import com.exponentialmana.mod.block.entity.ThawerBlockEntity;
import com.exponentialmana.mod.registry.ModItems;
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

public class ThawerMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOTS = ThawerBlockEntity.INPUT_SLOTS;   // 4
    public static final int OUTPUT_SLOTS = ThawerBlockEntity.OUTPUT_SLOTS; // 100
    public static final int TOTAL_MACHINE_SLOTS = ThawerBlockEntity.TOTAL_SLOTS; // 104

    private final ThawerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public ThawerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ThawerMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.THAWER_MENU.get(), containerId);
        this.blockEntity = (ThawerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // Left: 2x2 Input Slots (0..3) for Batch Compressed Mana
        int inStartX = 18;
        int inStartY = 70;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                int slotIndex = col + row * 2;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        inStartX + col * 18, inStartY + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(ModItems.BATCH_COMPRESSED_MANA.get());
                    }
                });
            }
        }

        // Right: 10x10 Output Slots (4..103)
        int outStartX = 68;
        int outStartY = 18;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int slotIndex = INPUT_SLOTS + col + row * 10;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        outStartX + col * 18, outStartY + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false; // Output slots only
                    }
                });
            }
        }

        // Player Inventory (3 rows x 9 columns)
        int playerInvX = 77;
        int playerInvY = 206;
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

    public ThawerBlockEntity getBlockEntity() {
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
                // Moving from machine to player inventory
                if (!this.moveItemStackTo(stackInSlot, TOTAL_MACHINE_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to machine
                if (stackInSlot.is(ModItems.BATCH_COMPRESSED_MANA.get())) {
                    if (!this.moveItemStackTo(stackInSlot, 0, INPUT_SLOTS, false)) {
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
