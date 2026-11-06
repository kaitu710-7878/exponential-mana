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
        this.bufferSlotStart = totalInputSlots;
        this.autoOutputSlotStart = bufferSlotStart + BUFFER_SLOTS;
        this.totalSlots = autoOutputSlotStart + AUTO_OUTPUT_SLOTS;

        List<Integer> outList = new ArrayList<>(AUTO_OUTPUT_SLOTS);
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
                if (slot >= bufferSlotStart) {
                    return Integer.MAX_VALUE; // Material Buffer and Auto-Output buffer hold up to Int_MAX
                }
                return 64; // Grid slots hold normal 64
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                // Auto-output buffer slots cannot be inserted into externally
                if (slot >= autoOutputSlotStart) {
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
                    slot -> slot >= autoOutputSlotStart && slot < totalSlots       // Output -> Auto Output Buffer (9 slots)
            )));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ExtendedCraftingTableBlockEntity be) {
        // 1. Auto-crafting (Crafts from Material Buffer into Auto-Output Buffer, never touching the grid)
        be.tickAutoCraft();

        // 2. Auto-eject products from auto-output buffer to adjacent inventories
        AutoEjectHelper.eject(level, pos, be.sideConfig, be.itemHandler, be.autoOutputIndices);
    }

    /**
     * Automated crafting engine:
     * - Only consumes materials from the Material Buffer (never touches the Grid slots).
     * - Automatically searches for craftable recipes for this tier based on buffer contents.
     * - Outputs crafted items strictly to the Auto-Output Buffer (never touches or clutters the manual craft result slot).
     */
    private void tickAutoCraft() {
        // Collect materials strictly from the Material Buffer (18 slots)
        Map<Item, Long> bufferMaterials = new HashMap<>();
        for (int b = bufferSlotStart; b < autoOutputSlotStart; b++) {
            ItemStack stack = itemHandler.getStackInSlot(b);
            if (!stack.isEmpty()) {
                bufferMaterials.put(stack.getItem(), bufferMaterials.getOrDefault(stack.getItem(), 0L) + stack.getCount());
            }
        }

        if (bufferMaterials.isEmpty()) return;

        ExtendedCraftingRecipe recipe = ExtendedCraftingRecipeManager.findMatchingRecipeFromMaterials(tier, bufferMaterials);
        if (recipe == null) return;

        ItemStack output = recipe.getOutput();
        if (output.isEmpty()) return;

        Map<Item, Integer> required = recipe.getRequiredIngredients();
        if (required.isEmpty()) return;

        // Calculate maximum crafts possible based on available materials in buffer
        long maxCrafts = Long.MAX_VALUE;
        for (Map.Entry<Item, Integer> req : required.entrySet()) {
            long avail = bufferMaterials.getOrDefault(req.getKey(), 0L);
            long possible = avail / req.getValue();
            if (possible < maxCrafts) {
                maxCrafts = possible;
            }
        }

        if (maxCrafts <= 0) return;

        // Calculate available output capacity in the 9 auto-output slots
        int outPerCraft = Math.max(1, output.getCount());
        long space = 0;
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

        // Consume materials strictly from Material Buffer
        for (Map.Entry<Item, Integer> req : required.entrySet()) {
            long toConsume = (long) req.getValue() * maxCrafts;
            for (int b = bufferSlotStart; b < autoOutputSlotStart && toConsume > 0; b++) {
                ItemStack inSlot = itemHandler.getStackInSlot(b);
                if (!inSlot.isEmpty() && inSlot.is(req.getKey())) {
                    int taken = (int) Math.min((long) inSlot.getCount(), toConsume);
                    inSlot.shrink(taken);
                    toConsume -= taken;
                }
            }
        }

        // Output products strictly into the 9 Auto-Output Buffer slots
        long toAdd = (long) outPerCraft * maxCrafts;

        // 1. Merge into existing matching stacks
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

        // 2. Put into empty auto-output slots
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
