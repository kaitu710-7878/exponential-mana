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
import net.minecraft.world.item.Item;
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
 * Architecture:
 * - Grid Slots: 0 .. totalInputSlots - 1 (Pattern definition and ingredients)
 * - Material Buffer: 18 slots (Int_MAX stack) for automated raw materials
 * - Auto-Craft Output Buffer: 9 slots (Int_MAX stack) for automated crafted products
 * - Full Mekanism-style Side Configuration (DISABLED, INPUT, OUTPUT, BOTH) with Auto-Eject.
 */
public class ExtendedCraftingTableBlockEntity extends BlockEntity implements MenuProvider, ISidedMachine {
    public static final int BUFFER_SLOTS = 18;
    public static final int AUTO_OUTPUT_SLOTS = 9;

    private final int tier;
    private final int gridSize;
    private final int totalInputSlots;
    private final int resultSlotIndex;
    private final int bufferSlotStart;
    private final int autoOutputSlotStart;
    private final int totalSlots;

    private final List<Integer> autoOutputIndices;

    private final SideConfig sideConfig = new SideConfig();

    private final ItemStackHandler itemHandler;
    private final Map<Direction, LazyOptional<IItemHandler>> sidedHandlers = new EnumMap<>(Direction.class);
    private final LazyOptional<IItemHandler> allAccessHandler;

    public ExtendedCraftingTableBlockEntity(BlockPos pos, BlockState state, int tier) {
        super(ModBlockEntities.EXTENDED_CRAFTING_TABLE_BE.get(), pos, state);
        this.tier = tier;
        this.gridSize = tier + 3;
        this.totalInputSlots = gridSize * gridSize;
        this.resultSlotIndex = totalInputSlots;
        this.bufferSlotStart = resultSlotIndex + 1;
        this.autoOutputSlotStart = bufferSlotStart + BUFFER_SLOTS;
        this.totalSlots = autoOutputSlotStart + AUTO_OUTPUT_SLOTS;

        List<Integer> outList = new ArrayList<>(1 + AUTO_OUTPUT_SLOTS);
        outList.add(resultSlotIndex);
        for (int i = autoOutputSlotStart; i < totalSlots; i++) {
            outList.add(i);
        }
        this.autoOutputIndices = Collections.unmodifiableList(outList);

        this.itemHandler = new ItemStackHandler(totalSlots) {
            @Override
            public void setSize(int size) {
                super.setSize(Math.max(totalSlots, size));
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot >= resultSlotIndex) {
                    return Integer.MAX_VALUE; // Result slot, Material Buffer and Auto-Output buffer hold up to Int_MAX
                }
                return 64; // Grid slots hold normal 64
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                // Result slot and auto-output buffer slots cannot be inserted into externally
                if (slot == resultSlotIndex || slot >= autoOutputSlotStart) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };

        this.allAccessHandler = LazyOptional.of(() -> itemHandler);

        for (Direction dir : Direction.values()) {
            this.sidedHandlers.put(dir, LazyOptional.of(() -> new SidedItemHandler(
                    itemHandler,
                    dir,
                    sideConfig,
                    slot -> slot >= bufferSlotStart && slot < autoOutputSlotStart, // Input -> Material Buffer (18 slots)
                    slot -> slot == resultSlotIndex || (slot >= autoOutputSlotStart && slot < totalSlots) // Output -> Result slot + Auto Output Buffer (9 slots)
            )));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ExtendedCraftingTableBlockEntity be) {
        // 1. Auto-crafting (Batch craft as many as possible per tick into the 9 auto-output buffer slots)
        be.tickAutoCraft();

        // 2. Auto-eject products from auto-output buffer to adjacent inventories
        AutoEjectHelper.eject(level, pos, be.sideConfig, be.itemHandler, be.autoOutputIndices);
    }

    /**
     * Crafts as many items as possible in 1 tick based on materials in the material buffer
     * and crafting grid. Outputs directly into the result slot (displayed in UI) and auto-output buffer.
     */
    private void tickAutoCraft() {
        // 1. Check if grid forms an explicit recipe
        ExtendedCraftingRecipe activeRecipe = ExtendedCraftingRecipeManager.findMatchingRecipe(tier, new IItemHandler() {
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

        // 2. If no valid recipe in grid, search recipe pool using items available in Material Buffer + Grid
        boolean fromGridPattern = (activeRecipe != null);
        if (activeRecipe == null) {
            Map<Item, Long> pool = new HashMap<>();
            for (int b = bufferSlotStart; b < autoOutputSlotStart; b++) {
                ItemStack stack = itemHandler.getStackInSlot(b);
                if (!stack.isEmpty()) {
                    pool.put(stack.getItem(), pool.getOrDefault(stack.getItem(), 0L) + stack.getCount());
                }
            }
            for (int g = 0; g < totalInputSlots; g++) {
                ItemStack stack = itemHandler.getStackInSlot(g);
                if (!stack.isEmpty()) {
                    pool.put(stack.getItem(), pool.getOrDefault(stack.getItem(), 0L) + stack.getCount());
                }
            }
            activeRecipe = ExtendedCraftingRecipeManager.findMatchingRecipeFromMaterials(tier, pool);
        }

        if (activeRecipe == null) return;

        ItemStack output = activeRecipe.getOutput();
        if (output.isEmpty()) return;

        Map<Item, Integer> required = activeRecipe.getRequiredIngredients();
        if (required.isEmpty()) return;

        // Count available items (Item -> Long)
        Map<Item, Long> available = new HashMap<>();
        // From Material Buffer: full amount
        for (int b = bufferSlotStart; b < autoOutputSlotStart; b++) {
            ItemStack stack = itemHandler.getStackInSlot(b);
            if (!stack.isEmpty()) {
                available.put(stack.getItem(), available.getOrDefault(stack.getItem(), 0L) + stack.getCount());
            }
        }
        // From Grid:
        for (int g = 0; g < totalInputSlots; g++) {
            ItemStack inGrid = itemHandler.getStackInSlot(g);
            if (!inGrid.isEmpty()) {
                // If crafting from grid pattern, preserve 1 item template if buffer has items,
                // but if buffer does not have it, allow consuming the grid items directly!
                long count = fromGridPattern ? (inGrid.getCount() > 1 ? inGrid.getCount() - 1 : 1) : inGrid.getCount();
                available.put(inGrid.getItem(), available.getOrDefault(inGrid.getItem(), 0L) + count);
            }
        }

        // Determine maximum possible crafts based on materials
        long maxCrafts = Long.MAX_VALUE;
        for (Map.Entry<Item, Integer> req : required.entrySet()) {
            long avail = available.getOrDefault(req.getKey(), 0L);
            long possible = avail / req.getValue();
            if (possible < maxCrafts) {
                maxCrafts = possible;
            }
        }

        if (maxCrafts <= 0) return;

        // Calculate available output capacity (Result Slot + 9 Auto-Output slots)
        int outPerCraft = Math.max(1, output.getCount());
        long space = 0;

        // Check Result Slot capacity
        ItemStack inRes = itemHandler.getStackInSlot(resultSlotIndex);
        if (inRes.isEmpty()) {
            space += Integer.MAX_VALUE;
        } else if (ItemStack.isSameItemSameTags(inRes, output)) {
            space += ((long) Integer.MAX_VALUE - inRes.getCount());
        }

        // Check Auto-Output slots capacity
        for (int o = autoOutputSlotStart; o < totalSlots; o++) {
            ItemStack inSlot = itemHandler.getStackInSlot(o);
            if (inSlot.isEmpty()) {
                space += Integer.MAX_VALUE;
            } else if (ItemStack.isSameItemSameTags(inSlot, output)) {
                space += ((long) Integer.MAX_VALUE - inSlot.getCount());
            }
        }

        long maxBySpace = space / outPerCraft;
        maxCrafts = Math.min(maxCrafts, maxBySpace);

        if (maxCrafts <= 0) return;

        // Consume required materials:
        // Prioritize consuming from Material Buffer, then consume from Grid!
        for (Map.Entry<Item, Integer> req : required.entrySet()) {
            long toConsume = (long) req.getValue() * maxCrafts;

            // 1. From Material Buffer
            for (int b = bufferSlotStart; b < autoOutputSlotStart && toConsume > 0; b++) {
                ItemStack inSlot = itemHandler.getStackInSlot(b);
                if (!inSlot.isEmpty() && inSlot.is(req.getKey())) {
                    int taken = (int) Math.min((long) inSlot.getCount(), toConsume);
                    inSlot.shrink(taken);
                    toConsume -= taken;
                }
            }

            // 2. From Grid
            for (int g = 0; g < totalInputSlots && toConsume > 0; g++) {
                ItemStack inGrid = itemHandler.getStackInSlot(g);
                if (!inGrid.isEmpty() && inGrid.is(req.getKey())) {
                    int taken = (int) Math.min((long) inGrid.getCount(), toConsume);
                    inGrid.shrink(taken);
                    toConsume -= taken;
                }
            }
        }

        // Add produced output:
        // 1. Into Result Slot (primary UI display slot)
        long toAdd = (long) outPerCraft * maxCrafts;
        ItemStack resSlot = itemHandler.getStackInSlot(resultSlotIndex);
        if (!resSlot.isEmpty() && ItemStack.isSameItemSameTags(resSlot, output)) {
            long room = (long) Integer.MAX_VALUE - resSlot.getCount();
            if (room > 0) {
                int add = (int) Math.min(room, toAdd);
                resSlot.grow(add);
                toAdd -= add;
            }
        } else if (resSlot.isEmpty()) {
            int add = (int) Math.min((long) Integer.MAX_VALUE, toAdd);
            ItemStack newStack = output.copy();
            newStack.setCount(add);
            itemHandler.setStackInSlot(resultSlotIndex, newStack);
            toAdd -= add;
        }

        // 2. Into 9 Auto-Output Buffer slots (merge existing)
        for (int o = autoOutputSlotStart; o < totalSlots && toAdd > 0; o++) {
            ItemStack inSlot = itemHandler.getStackInSlot(o);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameTags(inSlot, output)) {
                long room = (long) Integer.MAX_VALUE - inSlot.getCount();
                if (room > 0) {
                    int add = (int) Math.min(room, toAdd);
                    inSlot.grow(add);
                    toAdd -= add;
                }
            }
        }

        // 3. Into empty Auto-Output Buffer slots
        for (int o = autoOutputSlotStart; o < totalSlots && toAdd > 0; o++) {
            ItemStack inSlot = itemHandler.getStackInSlot(o);
            if (inSlot.isEmpty()) {
                int add = (int) Math.min((long) Integer.MAX_VALUE, toAdd);
                ItemStack newStack = output.copy();
                newStack.setCount(add);
                itemHandler.setStackInSlot(o, newStack);
                toAdd -= add;
            }
        }

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

    public int getResultSlotIndex() {
        return resultSlotIndex;
    }

    public int getBufferSlotStart() {
        return bufferSlotStart;
    }

    public int getAutoOutputSlotStart() {
        return autoOutputSlotStart;
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
        private final net.minecraft.world.item.Item item;
        private final CompoundTag tag;

        public ItemStackKey(ItemStack stack) {
            this.item = stack.getItem();
            // Normalize empty tags to null so empty CompoundTag equals null
            this.tag = (stack.hasTag() && !stack.getTag().isEmpty()) ? stack.getTag() : null;
        }

        public boolean matches(ItemStack other) {
            if (other.isEmpty() || !other.is(this.item)) return false;
            CompoundTag otherTag = (other.hasTag() && !other.getTag().isEmpty()) ? other.getTag() : null;
            if (this.tag != null || otherTag != null) {
                return Objects.equals(this.tag, otherTag);
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackKey that = (ItemStackKey) o;
            if (this.item != that.item) return false;
            return Objects.equals(this.tag, that.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, tag);
        }
    }
}
