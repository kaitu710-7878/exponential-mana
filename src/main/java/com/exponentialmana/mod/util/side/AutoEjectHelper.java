package com.exponentialmana.mod.util.side;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * Helper to auto-eject items from output slots to adjacent inventories matching SideConfig.
 */
public class AutoEjectHelper {

    /**
     * Attempts to eject items from the given source slots to any adjacent block on canOutput sides.
     * @param level World level
     * @param pos Current block position
     * @param sideConfig Machine side configuration
     * @param sourceHandler Machine internal item handler
     * @param outputSlots List of slot indices that should be auto-ejected
     */
    public static void eject(Level level, BlockPos pos, SideConfig sideConfig, IItemHandler sourceHandler, List<Integer> outputSlots) {
        if (level == null || level.isClientSide || !sideConfig.isAutoEject()) {
            return;
        }

        for (Direction dir : Direction.values()) {
            if (!sideConfig.canOutput(dir)) {
                continue;
            }

            BlockPos targetPos = pos.relative(dir);
            BlockEntity targetBe = level.getBlockEntity(targetPos);
            if (targetBe == null) continue;

            Direction targetFace = dir.getOpposite();
            targetBe.getCapability(ForgeCapabilities.ITEM_HANDLER, targetFace).ifPresent(targetHandler -> {
                for (int slotIndex : outputSlots) {
                    ItemStack inSlot = sourceHandler.getStackInSlot(slotIndex);
                    if (inSlot.isEmpty()) continue;

                    // Try to insert a stack (up to 64 or full stack at a time) into the adjacent handler
                    int amountToMove = Math.min(inSlot.getCount(), inSlot.getMaxStackSize());
                    ItemStack movingStack = inSlot.copyWithCount(amountToMove);

                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(targetHandler, movingStack, false);
                    int accepted = amountToMove - remainder.getCount();
                    if (accepted > 0) {
                        sourceHandler.extractItem(slotIndex, accepted, false);
                    }
                }
            });
        }
    }
}
