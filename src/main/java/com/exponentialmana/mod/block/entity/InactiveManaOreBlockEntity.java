package com.exponentialmana.mod.block.entity;

import com.exponentialmana.mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * BlockEntity for Inactive Mana Ore.
 * Handles the 5-minute (6000 ticks) countdown to activate into the normal tier ore.
 */
public class InactiveManaOreBlockEntity extends BlockEntity {
    public static final int ACTIVATION_TICKS = 6000; // 5 minutes (20 ticks/sec * 300 sec)
    private int progressTicks = 0;
    private final Supplier<Block> targetActiveOreSupplier;

    public InactiveManaOreBlockEntity(BlockPos pos, BlockState state, Supplier<Block> targetActiveOreSupplier) {
        super(ModBlockEntities.INACTIVE_MANA_ORE_BE.get(), pos, state);
        this.targetActiveOreSupplier = targetActiveOreSupplier;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InactiveManaOreBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }

        entity.progressTicks++;
        if (entity.progressTicks >= ACTIVATION_TICKS) {
            // Activate ore into normal ore
            Block targetBlock = null;
            if (entity.targetActiveOreSupplier != null) {
                targetBlock = entity.targetActiveOreSupplier.get();
            }
            if (targetBlock == null && state.getBlock() instanceof com.exponentialmana.mod.block.InactiveManaOreBlock inactiveBlock) {
                targetBlock = switch (inactiveBlock.getTier()) {
                    case 1 -> com.exponentialmana.mod.registry.ModBlocks.TIER1_MANA_ORE.get();
                    case 2 -> com.exponentialmana.mod.registry.ModBlocks.TIER2_MANA_ORE.get();
                    case 3 -> com.exponentialmana.mod.registry.ModBlocks.TIER3_MANA_ORE.get();
                    case 4 -> com.exponentialmana.mod.registry.ModBlocks.TIER4_MANA_ORE.get();
                    case 5 -> com.exponentialmana.mod.registry.ModBlocks.TIER5_MANA_ORE.get();
                    case 6 -> com.exponentialmana.mod.registry.ModBlocks.TIER6_MANA_ORE.get();
                    case 7 -> com.exponentialmana.mod.registry.ModBlocks.TIER7_MANA_ORE.get();
                    case 8 -> com.exponentialmana.mod.registry.ModBlocks.TIER8_MANA_ORE.get();
                    case 9 -> com.exponentialmana.mod.registry.ModBlocks.TIER9_MANA_ORE.get();
                    case 10 -> com.exponentialmana.mod.registry.ModBlocks.TIER10_MANA_ORE.get();
                    default -> null;
                };
            }

            if (targetBlock != null) {
                level.setBlockAndUpdate(pos, targetBlock.defaultBlockState());
            }
        }
    }

    public int getProgressTicks() {
        return progressTicks;
    }

    public int getRemainingSeconds() {
        return Math.max(0, (ACTIVATION_TICKS - progressTicks) / 20);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ProgressTicks", progressTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progressTicks = tag.getInt("ProgressTicks");
    }
}
