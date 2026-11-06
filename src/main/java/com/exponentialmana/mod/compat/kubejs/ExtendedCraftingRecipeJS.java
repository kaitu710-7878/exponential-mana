package com.exponentialmana.mod.compat.kubejs;

import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * Custom RecipeJS builder for Exponential Mana Extended Crafting recipes.
 * Allows fluent chaining in KubeJS server scripts:
 *   event.recipes.exponential_mana.tier1('result', ['AAAA', ...], { A: 'item' })
 *   event.recipes.exponential_mana.extended_crafting('result', 5, [...], {...}).tier(5)
 */
public class ExtendedCraftingRecipeJS extends RecipeJS {

    public ExtendedCraftingRecipeJS tier(int tier) {
        setValue(ExtendedCraftingRecipeSchema.TIER, Math.max(1, Math.min(10, tier)));
        return this;
    }

    public ExtendedCraftingRecipeJS shapeless() {
        setValue(ExtendedCraftingRecipeSchema.SHAPELESS, true);
        return this;
    }

    public ExtendedCraftingRecipeJS fullGrid() {
        setValue(ExtendedCraftingRecipeSchema.FULL_GRID, true);
        return this;
    }
}
