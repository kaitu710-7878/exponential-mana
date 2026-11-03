package com.exponentialmana.mod.menu;

import com.exponentialmana.mod.block.InactiveManaOreBlock;
import com.exponentialmana.mod.block.entity.BatchOreBreakerBlockEntity;
import com.exponentialmana.mod.registry.ModBlocks;
import com.exponentialmana.mod.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BatchOreBreakerMenu extends AbstractContainerMenu {
    private final BatchOreBreakerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public BatchOreBreakerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BatchOreBreakerMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.BATCH_ORE_BREAKER_MENU.get(), containerId);
        this.blockEntity = (BatchOreBreakerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // 10x10 Grid (100 Slots) for Inactive Ores
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int slotIndex = col + row * 10;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex, 10 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        if (stack.getItem() instanceof BlockItem bi) {
                            Block b = bi.getBlock();
                            return b instanceof InactiveManaOreBlock || b == ModBlocks.INACTIVE_TIER0_MANA_ORE.get();
                        }
                        return false;
                    }
                });
            }
        }

        // Player Inventory
        int playerInvY = 204;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 19 + col * 18, playerInvY + row * 18));
            }
        }

        // Player Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 19 + col * 18, playerInvY + 58));
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

            if (index < 100) {
                // Moving from machine to player inventory
                if (!this.moveItemStackTo(stackInSlot, 100, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to machine
                if (stackInSlot.getItem() instanceof BlockItem bi) {
                    Block b = bi.getBlock();
                    if (b instanceof InactiveManaOreBlock || b == ModBlocks.INACTIVE_TIER0_MANA_ORE.get()) {
                        if (!this.moveItemStackTo(stackInSlot, 0, 100, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
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
