package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.ExtendedCraftingMenu;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipe;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipeManager;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.util.side.AutoEjectHelper;
import com.exponentialmana.mod.util.side.ISidedMachine;
import com.exponentialmana.mod.util.side.SideConfig;
import com.exponentialmana.mod.util.side.SidedItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * BlockEntity for Extended Crafting Tables (Tier 1: 4x4 up to Tier 10: 13x13).
 * Features:
 * - Real-time Recipe Matching for Manual Crafting & JEI Transfer.
 * - Mekanism Formulaic Assemblicator-style automation: 18-slot Material Buffer with 1 craft/1t auto-crafting.
 * - Side Configuration (DISABLED, INPUT, OUTPUT, BOTH) with Auto-Eject.
 * - Int_MAX stack support for output and material buffer.
 */
public class ExtendedCraftingTableBlockEntity extends BlockEntity implements MenuProvider, ISidedMachine {
    public static final int BUFFER_SLOTS = 18;

    private final int tier;
    private final int gridSize;
    private final int totalInputSlots;
    private final int outputSlotIndex;
    private final int bufferSlotStart;
    private final int totalSlots;

    private final SideConfig sideConfig = new SideConfig();

    private final ItemStackHandler itemHandler;
    private final Map<Direction, LazyOptional<IItemHandler>> sidedHandlers = new EnumMap<>(Direction.class);
    private final LazyOptional<IItemHandler> allAccessHandler;

    public ExtendedCraftingTableBlockEntity(BlockPos pos, BlockState state, int tier) {
        super(ModBlockEntities.EXTENDED_CRAFTING_TABLE_BE.get(), pos, state);
        this.tier = tier;
        this.gridSize = tier + 3;
        this.totalInputSlots = gridSize * gridSize;
        this.outputSlotIndex = totalInputSlots;
        this.bufferSlotStart = totalInputSlots + 1;
        this.totalSlots = bufferSlotStart + BUFFER_SLOTS;

        this.itemHandler = new ItemStackHandler(totalSlots) {
            @Override
            public void setSize(int size) {
                super.setSize(Math.max(totalSlots, size));
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot == outputSlotIndex || (slot >= bufferSlotStart && slot < totalSlots)) {
                    return Integer.MAX_VALUE;
                }
                return 64; // Grid slots hold normal 64 for pattern
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                // External input only goes to buffer slots
                if (slot < bufferSlotStart) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (slot < totalInputSlots) {
                    // Grid changed: recalculate output if manually crafting
                    checkAndSetRecipeOutput();
                }
            }
        };

        this.allAccessHandler = LazyOptional.of(() -> itemHandler);

        for (Direction dir : Direction.values()) {
            this.sidedHandlers.put(dir, LazyOptional.of(() -> new SidedItemHandler(
                    itemHandler,
                    dir,
                    sideConfig,
                    slot -> slot >= bufferSlotStart && slot < totalSlots, // Input -> buffer
                    slot -> slot == outputSlotIndex                       // Output -> output slot
            )));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ExtendedCraftingTableBlockEntity be) {
        // 1. Auto-crafting (Mekanism Formulaic Assemblicator behavior at 1 craft / 1t)
        be.tickAutoCraft();

        // 2. Auto-eject to adjacent inventories
        AutoEjectHelper.eject(level, pos, be.sideConfig, be.itemHandler, Collections.singletonList(be.outputSlotIndex));
    }

    /**
     * Ticks 1 craft/1t automation using the material buffer.
     */
    private void tickAutoCraft() {
        ExtendedCraftingRecipe recipe = ExtendedCraftingRecipeManager.findMatchingRecipe(tier, new IItemHandler() {
            @Override
            public int getSlots() { return totalInputSlots; }
            @Override
            public @NotNull ItemStack getStackInSlot(int slot) { return itemHandler.getStackInSlot(slot); }
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            @Override
            public int getSlotLimit(int slot) { return 64; }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) { return true; }
        });

        if (recipe == null) return;

        ItemStack output = recipe.getOutput();
        ItemStack currentOut = itemHandler.getStackInSlot(outputSlotIndex);
        if (!currentOut.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(currentOut, output)) return;
            if (currentOut.getCount() >= Integer.MAX_VALUE) return;
        }

        // Count required items for this recipe
        Map<ItemStackKey, Integer> required = new HashMap<>();
        for (int i = 0; i < totalInputSlots; i++) {
            ItemStack inGrid = itemHandler.getStackInSlot(i);
            if (!inGrid.isEmpty()) {
                ItemStackKey key = new ItemStackKey(inGrid);
                required.put(key, required.getOrDefault(key, 0) + 1);
            }
        }

        if (required.isEmpty()) return;

        // Check if material buffer has all required items
        Map<ItemStackKey, Integer> available = new HashMap<>();
        for (int b = bufferSlotStart; b < totalSlots; b++) {
            ItemStack stack = itemHandler.getStackInSlot(b);
            if (!stack.isEmpty()) {
                ItemStackKey key = new ItemStackKey(stack);
                available.put(key, available.getOrDefault(key, 0) + stack.getCount());
            }
        }

        boolean canCraft = true;
        for (Map.Entry<ItemStackKey, Integer> req : required.entrySet()) {
            if (available.getOrDefault(req.getKey(), 0) < req.getValue()) {
                canCraft = false;
                break;
            }
        }

        if (!canCraft) return;

        // Consume required items from buffer
        for (Map.Entry<ItemStackKey, Integer> req : required.entrySet()) {
            int toConsume = req.getValue();
            for (int b = bufferSlotStart; b < totalSlots && toConsume > 0; b++) {
                ItemStack inSlot = itemHandler.getStackInSlot(b);
                if (!inSlot.isEmpty() && new ItemStackKey(inSlot).equals(req.getKey())) {
                    int taken = Math.min(inSlot.getCount(), toConsume);
                    inSlot.shrink(taken);
                    toConsume -= taken;
                }
            }
        }

        // Produce output
        if (currentOut.isEmpty()) {
            itemHandler.setStackInSlot(outputSlotIndex, output.copy());
        } else {
            currentOut.grow(output.getCount());
        }
        setChanged();
    }

    /**
     * Checks recipe and updates output slot for manual player crafting.
     */
    public void checkAndSetRecipeOutput() {
        ExtendedCraftingRecipe recipe = ExtendedCraftingRecipeManager.findMatchingRecipe(tier, new IItemHandler() {
            @Override
            public int getSlots() { return totalInputSlots; }
            @Override
            public @NotNull ItemStack getStackInSlot(int slot) { return itemHandler.getStackInSlot(slot); }
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            @Override
            public int getSlotLimit(int slot) { return 64; }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) { return true; }
        });

        if (recipe != null) {
            ItemStack out = recipe.getOutput();
            ItemStack current = itemHandler.getStackInSlot(outputSlotIndex);
            if (current.isEmpty()) {
                itemHandler.setStackInSlot(outputSlotIndex, out.copy());
            }
        }
    }

    public void onManualTake() {
        for (int i = 0; i < totalInputSlots; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                itemHandler.extractItem(i, 1, false);
            }
        }
        checkAndSetRecipeOutput();
        setChanged();
    }

    public int getTier() {
        return tier;
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

    public int getTotalSlots() {
        return totalSlots;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public SideConfig getSideConfig() {
        return sideConfig;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return allAccessHandler.cast();
            }
            if (sideConfig.getMode(side) == com.exponentialmana.mod.util.side.SideMode.DISABLED) {
                return LazyOptional.empty();
            }
            LazyOptional<IItemHandler> sided = sidedHandlers.get(side);
            if (sided != null) {
                return sided.cast();
            }
            return allAccessHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        allAccessHandler.invalidate();
        for (LazyOptional<IItemHandler> sided : sidedHandlers.values()) {
            sided.invalidate();
        }
    }

    public void saveToItem(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
        saveAdditional(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.exponential_mana.tier" + tier + "_extended_crafting_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ExtendedCraftingMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("side_config", sideConfig.serializeNBT());
        tag.putInt("tier", tier);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        if (itemHandler.getSlots() < totalSlots) {
            itemHandler.setSize(totalSlots);
        }
        if (tag.contains("side_config")) {
            sideConfig.deserializeNBT(tag.getCompound("side_config"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private static class ItemStackKey {
        private final ItemStack stack;

        public ItemStackKey(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackKey that = (ItemStackKey) o;
            return ItemStack.isSameItemSameTags(stack, that.stack);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stack.getItem(), stack.getTag());
        }
    }
}
