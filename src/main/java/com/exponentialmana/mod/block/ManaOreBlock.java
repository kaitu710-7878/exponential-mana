package com.exponentialmana.mod.block;

import com.exponentialmana.mod.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Standard Mana Ore block (Tier 0 to Tier 10, Pure Mana Ore).
 * Mining behavior:
 * - Tier 0: Directly drops Tier 0 Mana.
 * - Tier 1+: Guaranteed to drop Inactive Tier N Mana Ore + Tier N Mana (silk touch ignored).
 */
public class ManaOreBlock extends Block {
    private final int tier;
    private final Supplier<ItemStack> manaDropSupplier;
    private final Supplier<ItemStack> inactiveOreDropSupplier;

    public ManaOreBlock(int tier, float hardness, float resistance,
                        Supplier<ItemStack> manaDropSupplier,
                        @Nullable Supplier<ItemStack> inactiveOreDropSupplier) {
        super(BlockBehaviour.Properties.of()
                .strength(hardness, resistance)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE));
        this.tier = tier;
        this.manaDropSupplier = manaDropSupplier;
        this.inactiveOreDropSupplier = inactiveOreDropSupplier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide && !player.isCreative()) {
            // Drop mana item
            if (manaDropSupplier != null) {
                popResource(level, pos, manaDropSupplier.get());
            }
            // For Tier 1+, also drop Inactive Ore
            if (inactiveOreDropSupplier != null) {
                popResource(level, pos, inactiveOreDropSupplier.get());
            }
        }
    }
}
