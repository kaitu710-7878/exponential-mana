package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.FairyAttractorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fairy Attractor Block.
 * Lures 1 fairy every 5 minutes.
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
                ItemStack output = attractorBe.getItemHandler().getStackInSlot(0);
                if (!output.isEmpty()) {
                    if (!player.getInventory().add(output)) {
                        player.drop(output, false);
                    }
                    attractorBe.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
                    player.sendSystemMessage(Component.translatable("message.exponential_mana.attractor.collected"));
                } else {
                    player.sendSystemMessage(Component.translatable("message.exponential_mana.attractor.status", attractorBe.getRemainingSeconds()));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FairyAttractorBlockEntity attractorBe) {
                attractorBe.drops();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.fairy_attractor.desc"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
