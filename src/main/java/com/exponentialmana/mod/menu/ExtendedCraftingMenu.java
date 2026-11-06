package com.exponentialmana.mod.menu;

import com.exponentialmana.mod.block.entity.ExtendedCraftingTableBlockEntity;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipe;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipeManager;
import com.exponentialmana.mod.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ExtendedCraftingMenu extends AbstractContainerMenu {
    private final ExtendedCraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final Player player;

    private final int tier;
    private final int gridSize;
    private final int totalInputSlots;
    private final int bufferSlotStart;
    private final int autoOutputSlotStart;

    // Slot Indices in Menu:
    // 0 .. totalInputSlots - 1: Grid
    // totalInputSlots: Result Slot (manual crafting preview)
    // manualResultMenuIndex = totalInputSlots
    // bufferMenuStart = totalInputSlots + 1
    // autoOutputMenuStart = totalInputSlots + 19
    // playerInvMenuStart = totalInputSlots + 28
    private final int manualResultMenuIndex;
    private final int bufferMenuStart;
    private final int autoOutputMenuStart;
    private final int playerInvMenuStart;

    private final net.minecraft.world.inventory.ResultContainer resultContainer = new net.minecraft.world.inventory.ResultContainer();

    private final int gridStartX = 10;
    private final int gridStartY = 20;
    private final int bufferStartX = 10;
    private final int bufferStartY;
    private final int autoOutputStartX = 10;
    private final int autoOutputStartY;
    private final int playerInvX = 10;
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
        this.player = inv.player;

        this.tier = blockEntity.getTier();
        this.gridSize = blockEntity.getGridSize();
        this.totalInputSlots = blockEntity.getTotalInputSlots();
        this.bufferSlotStart = blockEntity.getBufferSlotStart();
        this.autoOutputSlotStart = blockEntity.getAutoOutputSlotStart();

        int gridWidth = gridSize * 18;
        int gridHeight = gridSize * 18;

        this.manualResultMenuIndex = totalInputSlots;
        this.bufferMenuStart = totalInputSlots + 1;
        this.autoOutputMenuStart = bufferMenuStart + ExtendedCraftingTableBlockEntity.BUFFER_SLOTS; // + 18
        this.playerInvMenuStart = autoOutputMenuStart + ExtendedCraftingTableBlockEntity.AUTO_OUTPUT_SLOTS; // + 9

        // Coordinates
        this.bufferStartY = gridStartY + gridHeight + 20;
        this.autoOutputStartY = bufferStartY + 2 * 18 + 14;
        this.playerInvY = autoOutputStartY + 18 + 14;

        this.imageWidth = Math.max(260, gridStartX + gridWidth + 76);
        this.imageHeight = playerInvY + 4 * 18 + 14;

        // 1. Grid Slots (0 .. totalInputSlots - 1)
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int slotIndex = col + row * gridSize;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slotIndex,
                        gridStartX + col * 18, gridStartY + row * 18) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        refreshRecipeResult();
                    }
                });
            }
        }

        // 2. Manual Result Slot (index: manualResultMenuIndex, for manual crafting from grid)
        int outX = gridStartX + gridWidth + 38;
        int outY = gridStartY + gridHeight / 2 - 8;
        this.addSlot(new Slot(resultContainer, 0, outX, outY) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
                consumeGridItems();
                super.onTake(pPlayer, pStack);
                refreshRecipeResult();
            }
        });

        // 3. Material Buffer (18 slots: 2 rows of 9)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                int bIndex = bufferSlotStart + col + row * 9;
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), bIndex,
                        bufferStartX + col * 18, bufferStartY + row * 18) {
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

        // 4. Auto-Craft Output Buffer (9 slots: 1 row of 9)
        for (int col = 0; col < 9; col++) {
            int oIndex = autoOutputSlotStart + col;
            this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), oIndex,
                    autoOutputStartX + col * 18, autoOutputStartY) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false; // Output buffer is for automated yields, cannot insert manually
                }

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

        // 5. Player Inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        // 6. Player Hotbar (1 row of 9)
        int hotbarY = playerInvY + 3 * 18 + 4;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, playerInvX + col * 18, hotbarY));
        }

        // Initial recipe check
        refreshRecipeResult();
    }

    private long lastGridHash = -1L;
    private ExtendedCraftingRecipe cachedMatchedRecipe = null;

    public void refreshRecipeResult() {
        ExtendedCraftingRecipe recipe = getMatchedRecipe();
        if (recipe != null) {
            resultContainer.setItem(0, recipe.getOutput().copy());
        } else {
            resultContainer.setItem(0, ItemStack.EMPTY);
        }
    }

    private ExtendedCraftingRecipe getMatchedRecipe() {
        long currentHash = computeGridHash();
        if (currentHash == lastGridHash) {
            return cachedMatchedRecipe;
        }
        lastGridHash = currentHash;
        cachedMatchedRecipe = ExtendedCraftingRecipeManager.findMatchingRecipe(tier, new IItemHandler() {
            @Override public int getSlots() { return totalInputSlots; }
            @Override public @NotNull ItemStack getStackInSlot(int slot) { return blockEntity.getItemHandler().getStackInSlot(slot); }
            @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
            @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            @Override public int getSlotLimit(int slot) { return 64; }
            @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return true; }
        });
        return cachedMatchedRecipe;
    }

    private long computeGridHash() {
        long h = 1L;
        for (int i = 0; i < totalInputSlots; i++) {
            ItemStack stack = blockEntity.getItemHandler().getStackInSlot(i);
            if (!stack.isEmpty()) {
                h = h * 31L + (long) System.identityHashCode(stack.getItem()) * 37L + stack.getCount();
            } else {
                h = h * 31L;
            }
        }
        return h;
    }

    private void consumeGridItems() {
        ExtendedCraftingRecipe recipe = getMatchedRecipe();
        if (recipe == null) return;

        if (recipe.isShapeless()) {
            java.util.Map<net.minecraft.world.item.Item, Integer> req = new java.util.HashMap<>(recipe.getRequiredIngredients());
            for (int i = 0; i < totalInputSlots && !req.isEmpty(); i++) {
                ItemStack stack = blockEntity.getItemHandler().getStackInSlot(i);
                if (!stack.isEmpty() && req.containsKey(stack.getItem())) {
                    int needed = req.get(stack.getItem());
                    int take = Math.min(needed, stack.getCount());
                    blockEntity.getItemHandler().extractItem(i, take, false);
                    if (needed - take <= 0) {
                        req.remove(stack.getItem());
                    } else {
                        req.put(stack.getItem(), needed - take);
                    }
                }
            }
        } else {
            for (int i = 0; i < totalInputSlots; i++) {
                ItemStack stack = blockEntity.getItemHandler().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    blockEntity.getItemHandler().extractItem(i, 1, false);
                }
            }
        }
        lastGridHash = -1L; // Invalidate cache after consumption
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        refreshRecipeResult();
    }

    @Override
    public void broadcastChanges() {
        refreshRecipeResult();
        super.broadcastChanges();
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

            int playerInvEnd = playerInvMenuStart + 36;

            if (index == manualResultMenuIndex) {
                // Taking from manual craft result slot
                if (!this.moveItemStackTo(slotStack, playerInvMenuStart, playerInvEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemstack);
                consumeGridItems();
                refreshRecipeResult();
                slot.onTake(player, slotStack);
                return itemstack;
            } else if (index < playerInvMenuStart) {
                // From Grid, Buffer, or Auto Output -> To Player Inventory
                if (!this.moveItemStackTo(slotStack, playerInvMenuStart, playerInvEnd, true)) {
                    return ItemStack.EMPTY;
                }
                if (index < totalInputSlots) {
                    refreshRecipeResult();
                }
            } else {
                // From Player Inventory -> Try Material Buffer first (supporting Int_MAX)
                int bufferEnd = bufferMenuStart + ExtendedCraftingTableBlockEntity.BUFFER_SLOTS;
                for (int b = bufferMenuStart; b < bufferEnd && !slotStack.isEmpty(); b++) {
                    Slot target = this.slots.get(b);
                    ItemStack tStack = target.getItem();
                    if (!tStack.isEmpty() && ItemStack.isSameItemSameTags(tStack, slotStack)) {
                        long space = (long) Integer.MAX_VALUE - tStack.getCount();
                        if (space > 0) {
                            int move = (int) Math.min(space, (long) slotStack.getCount());
                            tStack.grow(move);
                            slotStack.shrink(move);
                            target.setChanged();
                        }
                    }
                }
                for (int b = bufferMenuStart; b < bufferEnd && !slotStack.isEmpty(); b++) {
                    Slot target = this.slots.get(b);
                    if (!target.hasItem()) {
                        target.set(slotStack.copy());
                        slotStack.setCount(0);
                        target.setChanged();
                    }
                }

                // If still not empty, try Grid slots (0 .. totalInputSlots - 1)
                if (!slotStack.isEmpty()) {
                    if (!this.moveItemStackTo(slotStack, 0, totalInputSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                    refreshRecipeResult();
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

    public ExtendedCraftingTableBlockEntity getBlockEntity() { return blockEntity; }
    public int getTier() { return tier; }
    public int getGridSize() { return gridSize; }
    public int getTotalInputSlots() { return totalInputSlots; }
    public int getManualResultMenuIndex() { return manualResultMenuIndex; }
    public int getBufferMenuStart() { return bufferMenuStart; }
    public int getAutoOutputMenuStart() { return autoOutputMenuStart; }
    public int getPlayerInvMenuStart() { return playerInvMenuStart; }
    public int getGridStartX() { return gridStartX; }
    public int getGridStartY() { return gridStartY; }
    public int getBufferStartX() { return bufferStartX; }
    public int getBufferStartY() { return bufferStartY; }
    public int getAutoOutputStartX() { return autoOutputStartX; }
    public int getAutoOutputStartY() { return autoOutputStartY; }
    public int getPlayerInvX() { return playerInvX; }
    public int getPlayerInvY() { return playerInvY; }
    public int getImageWidth() { return imageWidth; }
    public int getImageHeight() { return imageHeight; }
}
