package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.FairyAttractorMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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

/**
 * BlockEntity for the Fairy Attractor.
 * Generates 1 Fairy Block every 5 minutes (6000 ticks).
 * Supports GUI and preserves contents on block pickup.
 */
public class FairyAttractorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int LURE_TICKS = 6000; // 5 minutes (300 sec * 20 ticks)
    private int timer = LURE_TICKS;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public FairyAttractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FAIRY_ATTRACTOR_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyAttractorBlockEntity entity) {
        if (level.isClientSide) return;

        entity.timer--;
        if (entity.timer <= 0) {
            entity.timer = LURE_TICKS;
            // Generate 1 Fairy Block
            ItemStack fairyStack = new ItemStack(ModBlocks.FAIRY_BLOCK.get());
            ItemStack current = entity.itemHandler.getStackInSlot(0);

            if (current.isEmpty()) {
                entity.itemHandler.setStackInSlot(0, fairyStack);
            } else if (current.is(ModBlocks.FAIRY_BLOCK.get().asItem()) && current.getCount() < current.getMaxStackSize()) {
                current.grow(1);
                entity.itemHandler.setStackInSlot(0, current);
            }
            entity.setChanged();
        }
    }

    public int getRemainingSeconds() {
        return Math.max(0, timer / 20);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        if (level != null) {
            Containers.dropContents(level, worldPosition, inventory);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void saveToItem(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
        saveAdditional(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.exponential_mana.fairy_attractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FairyAttractorMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("timer", timer);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        timer = tag.getInt("timer");
    }
}
