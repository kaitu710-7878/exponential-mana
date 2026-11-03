package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.FairyBlockEntity;
import com.exponentialmana.mod.util.CompactStackFormatter;
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
 * Fairy Block and Compressed Fairy Block.
 * Opens GUI on normal right-click. Preserves inventory in ItemStack NBT on removal.
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
                if (player instanceof ServerPlayer serverPlayer && !player.isShiftKeyDown()) {
                    NetworkHooks.openScreen(serverPlayer, fairyBe, pos);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && !player.isCreative() && blockEntity instanceof FairyBlockEntity fairyBe) {
            ItemStack dropStack = new ItemStack(this);
            fairyBe.saveToItem(dropStack);
            popResource(level, pos, dropStack);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FairyBlockEntity fairyBe) {
            if (!level.isClientSide && player.isCreative()) {
                ItemStack dropStack = new ItemStack(this);
                fairyBe.saveToItem(dropStack);
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
            if (be instanceof FairyBlockEntity fairyBe) {
                CompoundTag tag = stack.getTagElement("BlockEntityTag");
                if (tag != null) {
                    fairyBe.load(tag);
                    fairyBe.setChanged();
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Do NOT scatter internal items to world! Items are safely preserved inside the dropped block item NBT.
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

        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag beTag = stack.getTagElement("BlockEntityTag");
            if (beTag != null && beTag.contains("inventory")) {
                tooltip.add(Component.translatable("tooltip.exponential_mana.preserved_contents"));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
