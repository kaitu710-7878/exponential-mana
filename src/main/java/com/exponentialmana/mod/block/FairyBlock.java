package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.FairyBlockEntity;
import com.exponentialmana.mod.util.CompactStackFormatter;
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
 * Fairy Block and Compressed Fairy Block.
 * Handles insertion and status of Fairy Catalysts.
 */
public class FairyBlock extends BaseEntityBlock {
    private final int compressionTier;

    public FairyBlock(int compressionTier) {
        super(BlockBehaviour.Properties.of()
                .strength(2.0F, 6.0F)
                .sound(SoundType.AMETHYST)
                .lightLevel(state -> Math.min(15, 4 + compressionTier)));
        this.compressionTier = compressionTier;
    }

    public int getCompressionTier() {
        return compressionTier;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FairyBlockEntity(pos, state, compressionTier);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof FairyBlockEntity fairyBe) {
                FairyBlockEntity.tick(lvl, pos, st, fairyBe);
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FairyBlockEntity fairyBe) {
                ItemStack heldItem = player.getItemInHand(hand);

                if (player.isShiftKeyDown()) {
                    // Extract all produced mana
                    ItemStack output = fairyBe.getItemHandler().getStackInSlot(1);
                    if (!output.isEmpty()) {
                        if (!player.getInventory().add(output)) {
                            player.drop(output, false);
                        }
                        fairyBe.getItemHandler().setStackInSlot(1, ItemStack.EMPTY);
                        player.sendSystemMessage(Component.translatable("message.exponential_mana.fairy.collected_mana"));
                        return InteractionResult.SUCCESS;
                    }

                    // If output empty, retrieve catalyst
                    ItemStack catalyst = fairyBe.getItemHandler().getStackInSlot(0);
                    if (!catalyst.isEmpty()) {
                        if (!player.getInventory().add(catalyst)) {
                            player.drop(catalyst, false);
                        }
                        fairyBe.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
                        player.sendSystemMessage(Component.translatable("message.exponential_mana.fairy.retrieved_catalyst"));
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    // Insert catalyst
                    if (!heldItem.isEmpty() && FairyBlockEntity.isCatalyst(heldItem.getItem())) {
                        ItemStack currentCatalyst = fairyBe.getItemHandler().getStackInSlot(0);
                        if (currentCatalyst.isEmpty()) {
                            fairyBe.getItemHandler().setStackInSlot(0, heldItem.copy());
                            heldItem.setCount(0);
                            player.sendSystemMessage(Component.translatable("message.exponential_mana.fairy.inserted_catalyst"));
                            return InteractionResult.SUCCESS;
                        } else if (ItemStack.isSameItemSameTags(currentCatalyst, heldItem)) {
                            long total = (long) currentCatalyst.getCount() + heldItem.getCount();
                            currentCatalyst.setCount((int) Math.min((long) Integer.MAX_VALUE, total));
                            fairyBe.getItemHandler().setStackInSlot(0, currentCatalyst);
                            heldItem.setCount(0);
                            player.sendSystemMessage(Component.translatable("message.exponential_mana.fairy.inserted_catalyst"));
                            return InteractionResult.SUCCESS;
                        }
                    }

                    // Display status
                    ItemStack catalyst = fairyBe.getItemHandler().getStackInSlot(0);
                    ItemStack output = fairyBe.getItemHandler().getStackInSlot(1);

                    String catStr = catalyst.isEmpty() ? "None" : catalyst.getItem().getDescription().getString() + " x" + CompactStackFormatter.formatCount(catalyst.getCount());
                    String outStr = output.isEmpty() ? "None" : output.getItem().getDescription().getString() + " x" + CompactStackFormatter.formatCount(output.getCount());

                    player.sendSystemMessage(Component.translatable("message.exponential_mana.fairy.status", catStr, outStr, fairyBe.getCooldown() / 20));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FairyBlockEntity fairyBe) {
                fairyBe.drops();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        if (compressionTier == 0) {
            tooltip.add(Component.translatable("tooltip.exponential_mana.fairy_block.desc"));
        } else {
            long multiplier = 1;
            for (int i = 0; i < compressionTier; i++) multiplier *= 9L;
            tooltip.add(Component.translatable("tooltip.exponential_mana.compressed_fairy.desc", multiplier));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
