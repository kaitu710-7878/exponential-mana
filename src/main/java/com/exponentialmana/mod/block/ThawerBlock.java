package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.ThawerBlockEntity;
import com.exponentialmana.mod.registry.ModItems;
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
 * Thawer Block.
 * Unpacks Batch Compressed Mana safely into real items.
 */
public class ThawerBlock extends BaseEntityBlock {
    public ThawerBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(3.5F, 10.0F)
                .sound(SoundType.NETHERITE_BLOCK));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThawerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof ThawerBlockEntity thawerBe) {
                ThawerBlockEntity.tick(lvl, pos, st, thawerBe);
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ThawerBlockEntity thawerBe) {
                ItemStack heldItem = player.getItemInHand(hand);

                // Insert Batch Compressed Mana into input slot (slot 0)
                if (!heldItem.isEmpty() && heldItem.is(ModItems.BATCH_COMPRESSED_MANA.get())) {
                    ItemStack current = thawerBe.getItemHandler().getStackInSlot(0);
                    if (current.isEmpty()) {
                        thawerBe.getItemHandler().setStackInSlot(0, heldItem.split(1));
                        player.sendSystemMessage(Component.translatable("message.exponential_mana.thawer.inserted"));
                        return InteractionResult.SUCCESS;
                    }
                }

                // Collect output items
                boolean collected = false;
                for (int i = 1; i < ThawerBlockEntity.TOTAL_SLOTS; i++) {
                    ItemStack output = thawerBe.getItemHandler().getStackInSlot(i);
                    if (!output.isEmpty()) {
                        if (!player.getInventory().add(output)) {
                            player.drop(output, false);
                        }
                        thawerBe.getItemHandler().setStackInSlot(i, ItemStack.EMPTY);
                        collected = true;
                    }
                }

                if (collected) {
                    player.sendSystemMessage(Component.translatable("message.exponential_mana.thawer.collected"));
                } else {
                    ItemStack input = thawerBe.getItemHandler().getStackInSlot(0);
                    if (input.isEmpty()) {
                        player.sendSystemMessage(Component.translatable("message.exponential_mana.thawer.empty"));
                    } else {
                        player.sendSystemMessage(Component.translatable("message.exponential_mana.thawer.thawing"));
                    }
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
            if (be instanceof ThawerBlockEntity thawerBe) {
                thawerBe.drops();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.thawer.desc"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
