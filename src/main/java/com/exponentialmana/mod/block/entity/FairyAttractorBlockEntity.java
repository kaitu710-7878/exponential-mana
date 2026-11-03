package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.FairyAttractorMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModBlocks;
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
import net.minecraft.world.inventory.ContainerData;
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
 * Fairy Attractor BlockEntity.
 * Upgraded to 8x8 inventory (64 slots) with Integer.MAX_VALUE per slot.
 * Generates 9^compressionTier fairies every 5 minutes (6000 ticks).
 * Supports Mekanism-style Side Configuration and Auto-Eject.
 */
public class FairyAttractorBlockEntity extends BlockEntity implements MenuProvider, ISidedMachine {
    public static final int SLOTS = 64; // 8x8 slots
    public static final int LURE_TICKS = 6000; // 5 minutes (300 sec * 20 ticks)

    private final int compressionTier;
    private int timer = LURE_TICKS;

    private final SideConfig sideConfig = new SideConfig();
    private final List<Integer> outputIndices;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) return timer;
            if (index == 1) return LURE_TICKS;
            if (index == 2) return compressionTier;
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) timer = value;
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final Map<Direction, LazyOptional<IItemHandler>> sidedHandlers = new EnumMap<>(Direction.class);
    private final LazyOptional<IItemHandler> allAccessHandler;

    public FairyAttractorBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 0);
    }

    public FairyAttractorBlockEntity(BlockPos pos, BlockState state, int compressionTier) {
        super(ModBlockEntities.FAIRY_ATTRACTOR_BE.get(), pos, state);
        this.compressionTier = compressionTier;

        List<Integer> list = new ArrayList<>(SLOTS);
        for (int i = 0; i < SLOTS; i++) {
            list.add(i);
        }
        this.outputIndices = Collections.unmodifiableList(list);

        this.allAccessHandler = LazyOptional.of(() -> itemHandler);

        for (Direction dir : Direction.values()) {
            this.sidedHandlers.put(dir, LazyOptional.of(() -> new SidedItemHandler(
                    itemHandler,
                    dir,
                    sideConfig,
                    slot -> false, // No input needed
                    slot -> true   // All 64 slots can be extracted
            )));
        }
    }

    public int getCompressionTier() {
        return compressionTier;
    }

    public int getTimer() {
        return timer;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public SideConfig getSideConfig() {
        return sideConfig;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyAttractorBlockEntity be) {
        be.timer--;
        if (be.timer <= 0) {
            be.timer = LURE_TICKS;
            be.processLure();
        }

        // Auto-eject products to adjacent inventories every tick
        AutoEjectHelper.eject(level, pos, be.sideConfig, be.itemHandler, be.outputIndices);
    }

    private void processLure() {
        long multiplier = 1;
        for (int i = 0; i < compressionTier; i++) {
            multiplier *= 9L;
        }

        ItemStack fairyStack = new ItemStack(ModBlocks.FAIRY_BLOCK.get());
        insertOutputFairy(fairyStack, multiplier);
        setChanged();
    }

    private void insertOutputFairy(ItemStack baseFairy, long amount) {
        long remaining = amount;

        // 1. Merge with existing matching stacks up to Int_MAX
        for (int i = 0; i < SLOTS && remaining > 0; i++) {
            ItemStack inSlot = itemHandler.getStackInSlot(i);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameTags(inSlot, baseFairy)) {
                long room = (long) Integer.MAX_VALUE - inSlot.getCount();
                if (room > 0) {
                    int add = (int) Math.min(room, remaining);
                    inSlot.grow(add);
                    remaining -= add;
                }
            }
        }

        // 2. Put into empty slots
        for (int i = 0; i < SLOTS && remaining > 0; i++) {
            ItemStack inSlot = itemHandler.getStackInSlot(i);
            if (inSlot.isEmpty()) {
                int add = (int) Math.min((long) Integer.MAX_VALUE, remaining);
                ItemStack newStack = baseFairy.copy();
                newStack.setCount(add);
                itemHandler.setStackInSlot(i, newStack);
                remaining -= add;
            }
        }
    }

    @Override
    public Component getDisplayName() {
        if (compressionTier == 0) {
            return Component.translatable("block.exponential_mana.fairy_attractor");
        }
        return Component.translatable("block.exponential_mana.compressed_fairy_attractor_" + compressionTier);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FairyAttractorMenu(id, inv, this);
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("timer", timer);
        tag.put("side_config", sideConfig.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
        if (tag.contains("timer")) {
            timer = tag.getInt("timer");
        }
        if (tag.contains("side_config")) {
            sideConfig.deserializeNBT(tag.getCompound("side_config"));
        }
    }
}
