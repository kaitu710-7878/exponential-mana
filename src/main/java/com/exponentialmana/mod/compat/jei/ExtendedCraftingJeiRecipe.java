package com.exponentialmana.mod.compat.jei;

import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Data holder for an Extended Crafting recipe displayed in JEI.
 */
public class ExtendedCraftingJeiRecipe {
    private final int tier;
    private final int gridSize;
    private final List<List<ItemStack>> inputs; // gridSize * gridSize list of possible inputs
    private final ItemStack output;
    private final String descriptionKey;

    public ExtendedCraftingJeiRecipe(int tier, int gridSize, List<List<ItemStack>> inputs, ItemStack output, String descriptionKey) {
        this.tier = tier;
        this.gridSize = gridSize;
        this.inputs = inputs;
        this.output = output;
        this.descriptionKey = descriptionKey;
    }

    public int getTier() {
        return tier;
    }

    public int getGridSize() {
        return gridSize;
    }

    public List<List<ItemStack>> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }
}
