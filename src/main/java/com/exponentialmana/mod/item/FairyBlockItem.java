package com.exponentialmana.mod.item;

import com.exponentialmana.mod.registry.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Custom BlockItem for the Fairy Block.
 * Unconsumed when used in crafting recipes (e.g. producing Fairy Residue).
 */
public class FairyBlockItem extends BlockItem {
    public FairyBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return new ItemStack(ModBlocks.FAIRY_BLOCK.get());
    }
}
