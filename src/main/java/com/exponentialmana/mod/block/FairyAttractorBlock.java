package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.FairyAttractorBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fairy Attractor Block.
 * Lures 1 fairy every 5 minutes.
 * Opens GUI and preserves contents on block pickup.
 */
public class FairyAttractorBlock extends BaseEntityBlock {
    public FairyAttractorBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(3.5F, 10.0F)
                .sound(SoundType.METAL));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FairyAttractorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof FairyAttractorBlockEntity attractorBe) {
                FairyAttractorBlockEntity.tick(lvl, pos, st, attractorBe);
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FairyAttractorBlockEntity attractorBe) {
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHooks.openScreen(serverPlayer, attractorBe, pos);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && !player.isCreative() && blockEntity instanceof FairyAttractorBlockEntity attractorBe) {
            ItemStack dropStack = new ItemStack(this);
            attractorBe.saveToItem(dropStack);
            popResource(level, pos, dropStack);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FairyAttractorBlockEntity attractorBe) {
            if (!level.isClientSide && player.isCreative()) {
                ItemStack dropStack = new ItemStack(this);
                attractorBe.saveToItem(dropStack);
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

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FairyAttractorBlockEntity attractorBe) {
                CompoundTag tag = stack.getTagElement("BlockEntityTag");
                if (tag != null) {
                    attractorBe.load(tag);
                    attractorBe.setChanged();
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Preserved in ItemStack NBT
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.fairy_attractor.desc"));
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag beTag = stack.getTagElement("BlockEntityTag");
            if (beTag != null && beTag.contains("inventory")) {
                tooltip.add(Component.translatable("tooltip.exponential_mana.preserved_contents"));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
