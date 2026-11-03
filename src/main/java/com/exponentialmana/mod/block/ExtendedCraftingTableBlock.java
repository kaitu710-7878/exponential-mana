package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.ExtendedCraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Extended Crafting Table Block (Tier 1: 4x4 up to Tier 10: 13x13).
 * Opens dedicated grid GUI. Preserves contents via item NBT on pickup without scattering.
 */
public class ExtendedCraftingTableBlock extends BaseEntityBlock {
    private final int tier;
    private final int gridSize;

    public ExtendedCraftingTableBlock(int tier) {
        super(BlockBehaviour.Properties.of()
                .strength(4.0F, 10.0F)
                .sound(SoundType.WOOD));
        this.tier = tier;
        this.gridSize = tier + 3;
    }

    public int getTier() {
        return tier;
    }

    public int getGridSize() {
        return gridSize;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExtendedCraftingTableBlockEntity(pos, state, tier);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ExtendedCraftingTableBlockEntity tableBe) {
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHooks.openScreen(serverPlayer, tableBe, pos);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && !player.isCreative() && blockEntity instanceof ExtendedCraftingTableBlockEntity tableBe) {
            ItemStack dropStack = new ItemStack(this);
            tableBe.saveToItem(dropStack);
            popResource(level, pos, dropStack);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ExtendedCraftingTableBlockEntity tableBe) {
            if (!level.isClientSide && player.isCreative()) {
                ItemStack dropStack = new ItemStack(this);
                tableBe.saveToItem(dropStack);
                if (dropStack.hasTag() && dropStack.getTag().contains("BlockEntityTag")) {
                    CompoundTag tag = dropStack.getTagElement("BlockEntityTag");
                    if (tag != null && tag.contains("inventory")) {
                        ItemEntity itemEntity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, dropStack);
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                    }
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, com.exponentialmana.mod.registry.ModBlockEntities.EXTENDED_CRAFTING_TABLE_BE.get(),
                ExtendedCraftingTableBlockEntity::serverTick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ExtendedCraftingTableBlockEntity tableBe) {
                CompoundTag tag = stack.getTagElement("BlockEntityTag");
                if (tag != null) {
                    tableBe.load(tag);
                    tableBe.setChanged();
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Safely preserved in ItemStack NBT
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.extended_crafting_table.desc",
                gridSize, gridSize, gridSize * gridSize));
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag beTag = stack.getTagElement("BlockEntityTag");
            if (beTag != null && beTag.contains("inventory")) {
                tooltip.add(Component.translatable("tooltip.exponential_mana.preserved_contents"));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
