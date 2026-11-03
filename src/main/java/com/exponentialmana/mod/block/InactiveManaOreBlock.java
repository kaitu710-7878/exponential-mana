package com.exponentialmana.mod.block;

import com.exponentialmana.mod.block.entity.InactiveManaOreBlockEntity;
import com.exponentialmana.mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Inactive Mana Ore block that takes 5 minutes of real-world game time to activate.
 * Obsidian-level hardness (50.0F), explosion resistance (1200.0F).
 */
public class InactiveManaOreBlock extends Block implements EntityBlock {
    private final int tier;
    private final Supplier<Block> activeOreSupplier;

    public InactiveManaOreBlock(int tier, Supplier<Block> activeOreSupplier) {
        super(BlockBehaviour.Properties.of()
                .strength(50.0F, 1200.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE));
        this.tier = tier;
        this.activeOreSupplier = activeOreSupplier;
    }

    public int getTier() {
        return tier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InactiveManaOreBlockEntity(pos, state, activeOreSupplier);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof InactiveManaOreBlockEntity inactiveBe) {
                InactiveManaOreBlockEntity.tick(lvl, pos, st, inactiveBe);
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.inactive_ore.desc"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
