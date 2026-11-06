package com.exponentialmana.mod.compat.kubejs;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TinyMap;

import java.util.HashMap;
import java.util.Map;

/**
 * RecipeSchema definitions for Exponential Mana Extended Crafting (Tier 1 to Tier 10).
 */
public interface ExtendedCraftingRecipeSchema {
    RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
    RecipeKey<Integer> TIER = NumberComponent.intRange(1, 10).key("tier").optional(1);
    RecipeKey<String[]> PATTERN = StringComponent.NON_EMPTY.asArray().key("pattern").optional(new String[0]);
    RecipeKey<TinyMap<Character, InputItem>> KEY = MapRecipeComponent.ITEM_PATTERN_KEY.key("key").optional(TinyMap.ofMap(Map.of()));
    RecipeKey<Boolean> SHAPELESS = BooleanComponent.BOOLEAN.key("shapeless").optional(false);
    RecipeKey<Boolean> FULL_GRID = BooleanComponent.BOOLEAN.key("full_grid").optional(false);
    RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key("ingredient").optional(InputItem.EMPTY);
    RecipeKey<InputItem[]> INGREDIENTS = ItemComponents.INPUT_ARRAY.key("ingredients").optional(new InputItem[0]);

    // General Extended Crafting Schema
    RecipeSchema SCHEMA = new RecipeSchema(
            ExtendedCraftingRecipeJS.class,
            ExtendedCraftingRecipeJS::new,
            RESULT, TIER, PATTERN, KEY, SHAPELESS, FULL_GRID, INGREDIENT, INGREDIENTS
    ).constructor(RESULT, TIER, PATTERN, KEY)
     .constructor(RESULT, PATTERN, KEY)
     .uniqueOutputId(RESULT);

    // Full-grid compression schema: event.recipes.exponential_mana.full_grid(result, tier, ingredient)
    RecipeSchema FULL_GRID_SCHEMA = new RecipeSchema(
            ExtendedCraftingRecipeJS.class,
            () -> {
                ExtendedCraftingRecipeJS r = new ExtendedCraftingRecipeJS();
                r.setValue(FULL_GRID, true);
                return r;
            },
            RESULT, TIER, INGREDIENT, FULL_GRID
    ).constructor(RESULT, TIER, INGREDIENT)
     .uniqueOutputId(RESULT);

    // Shapeless schema: event.recipes.exponential_mana.shapeless(result, tier, ingredients)
    RecipeSchema SHAPELESS_SCHEMA = new RecipeSchema(
            ExtendedCraftingRecipeJS.class,
            () -> {
                ExtendedCraftingRecipeJS r = new ExtendedCraftingRecipeJS();
                r.setValue(SHAPELESS, true);
                return r;
            },
            RESULT, TIER, INGREDIENTS, SHAPELESS
    ).constructor(RESULT, TIER, INGREDIENTS)
     .uniqueOutputId(RESULT);

    Map<Integer, RecipeSchema> TIER_SCHEMAS = new HashMap<>();

    static RecipeSchema tierSchema(int tier) {
        return TIER_SCHEMAS.computeIfAbsent(tier, t -> {
            RecipeKey<Integer> fixedTierKey = NumberComponent.intRange(1, 10).key("tier").optional(t);
            return new RecipeSchema(
                    ExtendedCraftingRecipeJS.class,
                    () -> {
                        ExtendedCraftingRecipeJS r = new ExtendedCraftingRecipeJS();
                        r.setValue(TIER, t);
                        return r;
                    },
                    RESULT, fixedTierKey, PATTERN, KEY, SHAPELESS, FULL_GRID
            ).constructor(RESULT, PATTERN, KEY)
             .uniqueOutputId(RESULT);
        });
    }
}
