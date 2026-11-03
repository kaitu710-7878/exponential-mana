package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.ExtendedCraftingTableBlockEntity;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Extended Crafting Table Block (Tier 1: 4x4 up to Tier 10: 13x13).
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
                if (player.isShiftKeyDown()) {
                    // Extract items
                    boolean extracted = false;
                    for (int i = 0; i <= tableBe.getTotalInputSlots(); i++) {
                        ItemStack stack = tableBe.getItemHandler().getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            if (!player.getInventory().add(stack)) {
                                player.drop(stack, false);
                            }
                            tableBe.getItemHandler().setStackInSlot(i, ItemStack.EMPTY);
                            extracted = true;
                        }
                    }
                    if (extracted) {
                        player.sendSystemMessage(Component.translatable("message.exponential_mana.crafting_table.extracted"));
                    } else {
                        player.sendSystemMessage(Component.translatable("message.exponential_mana.crafting_table.empty"));
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    int filledSlots = 0;
                    for (int i = 0; i < tableBe.getTotalInputSlots(); i++) {
                        if (!tableBe.getItemHandler().getStackInSlot(i).isEmpty()) {
                            filledSlots++;
                        }
                    }
                    ItemStack output = tableBe.getItemHandler().getStackInSlot(tableBe.getOutputSlotIndex());
                    String outDesc = output.isEmpty() ? "None" : output.getItem().getDescription().getString() + " x" + output.getCount();

                    player.sendSystemMessage(Component.translatable("message.exponential_mana.crafting_table.status",
                            gridSize, gridSize, filledSlots, tableBe.getTotalInputSlots(), outDesc));
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
            if (be instanceof ExtendedCraftingTableBlockEntity tableBe) {
                tableBe.drops();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.extended_crafting_table.desc",
                gridSize, gridSize, gridSize * gridSize));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
