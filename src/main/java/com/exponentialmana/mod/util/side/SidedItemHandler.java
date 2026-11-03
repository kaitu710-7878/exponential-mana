package com.exponentialmana.mod.util.side;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntPredicate;

/**
 * Direction-filtered wrapper for IItemHandler based on SideMode.
 */
public class SidedItemHandler implements IItemHandler {
    private final IItemHandler internal;
    private final Direction side;
    private final SideConfig sideConfig;
    private final IntPredicate isInputSlot;
    private final IntPredicate isOutputSlot;

    public SidedItemHandler(IItemHandler internal, Direction side, SideConfig sideConfig,
                            IntPredicate isInputSlot, IntPredicate isOutputSlot) {
        this.internal = internal;
        this.side = side;
        this.sideConfig = sideConfig;
        this.isInputSlot = isInputSlot;
        this.isOutputSlot = isOutputSlot;
    }

    @Override
    public int getSlots() {
        if (side != null && sideConfig.getMode(side) == SideMode.DISABLED) {
            return 0;
        }
        return internal.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (side != null && sideConfig.getMode(side) == SideMode.DISABLED) {
            return ItemStack.EMPTY;
        }
        return internal.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (side != null && !sideConfig.canInput(side)) {
            return stack;
        }
        if (!isInputSlot.test(slot)) {
            return stack; // Not an import-allowed slot for this machine
        }
        return internal.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (side != null && !sideConfig.canOutput(side)) {
            return ItemStack.EMPTY;
        }
        if (!isOutputSlot.test(slot)) {
            return ItemStack.EMPTY; // Not an export-allowed slot for this machine
        }
        return internal.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return internal.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (side != null && !sideConfig.canInput(side)) {
            return false;
        }
        if (!isInputSlot.test(slot)) {
            return false;
        }
        return internal.isItemValid(slot, stack);
    }
}
