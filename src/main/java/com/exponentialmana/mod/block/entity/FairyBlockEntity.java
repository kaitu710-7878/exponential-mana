package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
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

/**
 * BlockEntity for Fairy Blocks and Compressed Fairy Blocks.
 * Automatically generates tiered mana every 60 seconds (1200 ticks) without consuming catalysts.
 * Supports Int_MAX catalyst capacity and 9^compression production multiplier.
 */
public class FairyBlockEntity extends BlockEntity {
    public static final int BASE_COOLDOWN = 1200; // 60 seconds at 20 TPS
    private int cooldown = BASE_COOLDOWN;
    private final int compressionTier; // 0 for base, 1 to 10 for compressed

    // Slot 0: Catalyst (Int_MAX limit, not consumed)
    // Slot 1: Output Mana (Int_MAX limit)
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return isCatalyst(stack.getItem());
            }
            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public FairyBlockEntity(BlockPos pos, BlockState state, int compressionTier) {
        super(ModBlockEntities.FAIRY_BLOCK_BE.get(), pos, state);
        this.compressionTier = compressionTier;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }

        ItemStack catalystStack = entity.itemHandler.getStackInSlot(0);
        if (!catalystStack.isEmpty() && isCatalyst(catalystStack.getItem())) {
            entity.cooldown--;
            if (entity.cooldown <= 0) {
                entity.cooldown = BASE_COOLDOWN;
                entity.produceMana(catalystStack);
            }
        } else {
            // Reset cooldown when no catalyst is present
            entity.cooldown = BASE_COOLDOWN;
        }
    }

    private void produceMana(ItemStack catalystStack) {
        Item manaItem = getManaForCatalyst(catalystStack.getItem());
        if (manaItem == null) return;

        // Calculate output: 9^compressionTier * catalystCount (capped at Integer.MAX_VALUE)
        long multiplier = 1;
        for (int i = 0; i < compressionTier; i++) {
            multiplier *= 9L;
        }

        long catalystCount = catalystStack.getCount();
        long totalProduced = Math.min((long) Integer.MAX_VALUE, multiplier * catalystCount);

        ItemStack currentOutput = itemHandler.getStackInSlot(1);
        if (currentOutput.isEmpty()) {
            itemHandler.setStackInSlot(1, new ItemStack(manaItem, (int) totalProduced));
        } else if (currentOutput.is(manaItem)) {
            long newCount = (long) currentOutput.getCount() + totalProduced;
            currentOutput.setCount((int) Math.min((long) Integer.MAX_VALUE, newCount));
            itemHandler.setStackInSlot(1, currentOutput);
        }
        setChanged();
    }

    public static boolean isCatalyst(Item item) {
        return item == ModItems.TIER1_FAIRY_CATALYST.get() ||
                item == ModItems.TIER2_FAIRY_CATALYST.get() ||
                item == ModItems.TIER3_FAIRY_CATALYST.get() ||
                item == ModItems.TIER4_FAIRY_CATALYST.get() ||
                item == ModItems.TIER5_FAIRY_CATALYST.get() ||
                item == ModItems.TIER6_FAIRY_CATALYST.get() ||
                item == ModItems.TIER7_FAIRY_CATALYST.get() ||
                item == ModItems.TIER8_FAIRY_CATALYST.get() ||
                item == ModItems.TIER9_FAIRY_CATALYST.get() ||
                item == ModItems.TIER10_FAIRY_CATALYST.get() ||
                item == ModItems.PURE_FAIRY_CATALYST.get();
    }

    @Nullable
    public static Item getManaForCatalyst(Item catalyst) {
        if (catalyst == ModItems.TIER1_FAIRY_CATALYST.get()) return ModItems.TIER1_MANA.get();
        if (catalyst == ModItems.TIER2_FAIRY_CATALYST.get()) return ModItems.TIER2_MANA.get();
        if (catalyst == ModItems.TIER3_FAIRY_CATALYST.get()) return ModItems.TIER3_MANA.get();
        if (catalyst == ModItems.TIER4_FAIRY_CATALYST.get()) return ModItems.TIER4_MANA.get();
        if (catalyst == ModItems.TIER5_FAIRY_CATALYST.get()) return ModItems.TIER5_MANA.get();
        if (catalyst == ModItems.TIER6_FAIRY_CATALYST.get()) return ModItems.TIER6_MANA.get();
        if (catalyst == ModItems.TIER7_FAIRY_CATALYST.get()) return ModItems.TIER7_MANA.get();
        if (catalyst == ModItems.TIER8_FAIRY_CATALYST.get()) return ModItems.TIER8_MANA.get();
        if (catalyst == ModItems.TIER9_FAIRY_CATALYST.get()) return ModItems.TIER9_MANA.get();
        if (catalyst == ModItems.TIER10_FAIRY_CATALYST.get()) return ModItems.TIER10_MANA.get();
        if (catalyst == ModItems.PURE_FAIRY_CATALYST.get()) return ModItems.PURE_MANA.get();
        return null;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCompressionTier() {
        return compressionTier;
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("cooldown", cooldown);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        cooldown = tag.getInt("cooldown");
    }
}
