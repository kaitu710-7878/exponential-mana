package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.menu.ThawerMenu;
import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModItems;
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
 * BlockEntity for the Thawer (解凍機).
 * Features 2x2 (4 slots) input for Batch Compressed Mana, and 10x10 (100 slots) output,
 * each output slot supporting up to Integer.MAX_VALUE stacks.
 * Safely extracts real item-form mana from Batch Compressed Mana into 100 output slots.
 * Full Mekanism-style Side Configuration and Auto-Eject.
 */
public class ThawerBlockEntity extends BlockEntity implements MenuProvider, ISidedMachine {
    public static final int INPUT_SLOTS = 4;   // 2x2 grid
    public static final int OUTPUT_SLOTS = 100; // 10x10 grid
    public static final int TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS; // 104

    private static final List<Integer> OUTPUT_SLOT_INDICES;
    static {
        List<Integer> list = new ArrayList<>(OUTPUT_SLOTS);
        for (int i = INPUT_SLOTS; i < TOTAL_SLOTS; i++) {
            list.add(i);
        }
        OUTPUT_SLOT_INDICES = Collections.unmodifiableList(list);
    }

    private final SideConfig sideConfig = new SideConfig();

    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        public void setSize(int size) {
            super.setSize(Math.max(TOTAL_SLOTS, size));
        }

        @Override
        public int getSlotLimit(int slot) {
            return (slot < INPUT_SLOTS) ? 64 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < INPUT_SLOTS) {
                return stack.is(ModItems.BATCH_COMPRESSED_MANA.get());
            }
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final Map<Direction, LazyOptional<IItemHandler>> sidedHandlers = new EnumMap<>(Direction.class);
    private final LazyOptional<IItemHandler> allAccessHandler = LazyOptional.of(() -> itemHandler);

    public ThawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THAWER_BE.get(), pos, state);

        for (Direction dir : Direction.values()) {
            this.sidedHandlers.put(dir, LazyOptional.of(() -> new SidedItemHandler(
                    itemHandler,
                    dir,
                    sideConfig,
                    slot -> slot < INPUT_SLOTS,              // Input -> compressed mana
                    slot -> slot >= INPUT_SLOTS && slot < TOTAL_SLOTS // Output -> extracted mana
            )));
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ThawerBlockEntity entity) {
        if (level.isClientSide) return;

        boolean changed = false;

        // Process each input slot
        for (int inSlot = 0; inSlot < INPUT_SLOTS; inSlot++) {
            ItemStack input = entity.itemHandler.getStackInSlot(inSlot);
            if (!input.isEmpty() && input.is(ModItems.BATCH_COMPRESSED_MANA.get())) {
                CompoundTag tag = input.getTag();
                if (tag != null && tag.contains("ManaCounts")) {
                    CompoundTag manaTag = tag.getCompound("ManaCounts");
                    boolean anyRemaining = false;

                    for (int tier = 0; tier <= 10; tier++) {
                        String key = "Tier" + tier;
                        long count = manaTag.getLong(key);
                        if (count > 0) {
                            Item manaItem = getManaItem(tier);
                            // Extract up to 64 per tick per tier to prevent massive single-tick freeze
                            int toExtract = (int) Math.min(count, 64L);
                            int inserted = entity.insertOutputMana(manaItem, toExtract);

                            if (inserted > 0) {
                                count -= inserted;
                                if (count <= 0) {
                                    manaTag.remove(key);
                                } else {
                                    manaTag.putLong(key, count);
                                }
                                changed = true;
                            }
                            if (count > 0) {
                                anyRemaining = true;
                            }
                        }
                    }

                    if (!anyRemaining) {
                        input.shrink(1);
                        changed = true;
                    }
                } else {
                    input.shrink(1);
                    changed = true;
                }
            }
        }

        if (changed) {
            entity.setChanged();
        }

        // Auto-eject extracted mana to adjacent inventories
        AutoEjectHelper.eject(level, pos, entity.sideConfig, entity.itemHandler, OUTPUT_SLOT_INDICES);
    }

    private int insertOutputMana(Item manaItem, int amount) {
        int remaining = amount;

        // Try existing stacks first
        for (int i = INPUT_SLOTS; i < TOTAL_SLOTS && remaining > 0; i++) {
            ItemStack inSlot = itemHandler.getStackInSlot(i);
            if (!inSlot.isEmpty() && inSlot.is(manaItem)) {
                int space = Integer.MAX_VALUE - inSlot.getCount();
                if (space > 0) {
                    int add = Math.min(space, remaining);
                    inSlot.grow(add);
                    remaining -= add;
                }
            }
        }

        // Put in empty slots
        for (int i = INPUT_SLOTS; i < TOTAL_SLOTS && remaining > 0; i++) {
            ItemStack inSlot = itemHandler.getStackInSlot(i);
            if (inSlot.isEmpty()) {
                itemHandler.setStackInSlot(i, new ItemStack(manaItem, remaining));
                remaining = 0;
            }
        }

        return amount - remaining;
    }

    private static Item getManaItem(int tier) {
        return switch (tier) {
            case 0 -> ModItems.TIER0_MANA.get();
            case 1 -> ModItems.TIER1_MANA.get();
            case 2 -> ModItems.TIER2_MANA.get();
            case 3 -> ModItems.TIER3_MANA.get();
            case 4 -> ModItems.TIER4_MANA.get();
            case 5 -> ModItems.TIER5_MANA.get();
            case 6 -> ModItems.TIER6_MANA.get();
            case 7 -> ModItems.TIER7_MANA.get();
            case 8 -> ModItems.TIER8_MANA.get();
            case 9 -> ModItems.TIER9_MANA.get();
            case 10 -> ModItems.TIER10_MANA.get();
            default -> ModItems.TIER0_MANA.get();
        };
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
        return Component.translatable("block.exponential_mana.thawer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ThawerMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("side_config", sideConfig.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        if (itemHandler.getSlots() < TOTAL_SLOTS) {
            itemHandler.setSize(TOTAL_SLOTS);
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
}
