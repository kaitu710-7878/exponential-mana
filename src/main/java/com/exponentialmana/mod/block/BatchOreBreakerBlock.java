package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.BatchOreBreakerBlockEntity;
import com.exponentialmana.mod.util.CompactStackFormatter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
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
 * Batch Ore Breaker Block.
 * Processes 10x10 slots of inactive ores into compressed mana.
 * Does NOT scatter items on break; drops a single NBT-packed Batch Compressed Mana.
 */
public class BatchOreBreakerBlock extends BaseEntityBlock {
    public BatchOreBreakerBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(5.0F, 12.0F)
                .sound(SoundType.NETHERITE_BLOCK));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatchOreBreakerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof BatchOreBreakerBlockEntity breakerBe) {
                BatchOreBreakerBlockEntity.tick(lvl, pos, st, breakerBe);
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BatchOreBreakerBlockEntity breakerBe) {
                ItemStack heldItem = player.getItemInHand(hand);

                // Quick insert if holding inactive ore
                if (!heldItem.isEmpty() && heldItem.getItem() instanceof BlockItem bi &&
                        (bi.getBlock() instanceof InactiveManaOreBlock || bi.getBlock() == com.exponentialmana.mod.registry.ModBlocks.INACTIVE_TIER0_MANA_ORE.get())) {
                    
                    ItemStack remaining = heldItem.copy();
                    for (int slot = 0; slot < BatchOreBreakerBlockEntity.SLOTS; slot++) {
                        remaining = breakerBe.getItemHandler().insertItem(slot, remaining, false);
                        if (remaining.isEmpty()) break;
                    }

                    heldItem.setCount(remaining.getCount());
                    player.sendSystemMessage(Component.translatable("message.exponential_mana.breaker.inserted"));
                    return InteractionResult.SUCCESS;
                }

                // Show status
                long[] pool = breakerBe.getManaPool();
                long totalMana = 0;
                for (long c : pool) totalMana += c;

                player.sendSystemMessage(Component.translatable("message.exponential_mana.breaker.status",
                        CompactStackFormatter.formatCount(totalMana)));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && blockEntity instanceof BatchOreBreakerBlockEntity breakerBe) {
            ItemStack drop = breakerBe.createDropStack();
            popResource(level, pos, drop);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.batch_ore_breaker.desc"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
