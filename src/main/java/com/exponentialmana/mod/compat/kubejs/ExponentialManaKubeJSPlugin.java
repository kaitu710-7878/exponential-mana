package com.exponentialmana.mod.compat.kubejs;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.block.entity.BatchOreBreakerBlockEntity;
import com.exponentialmana.mod.block.entity.FairyBlockEntity;
import com.exponentialmana.mod.registry.ModBlocks;
import com.exponentialmana.mod.registry.ModItems;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;

/**
 * KubeJS Plugin for Exponential Mana.
 * Exposes recipes, table builders, and custom registration APIs to KubeJS scripts.
 */
public class ExponentialManaKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        RecipeNamespace ns = event.namespace(ExponentialMana.MOD_ID);

        // 1. General extended crafting builder: event.recipes.exponential_mana.extended_crafting(...)
        ns.register("extended_crafting", ExtendedCraftingRecipeSchema.SCHEMA);

        // 2. Direct tier-specific builders: event.recipes.exponential_mana.tier1(...) up to tier10(...)
        for (int tier = 1; tier <= 10; tier++) {
            ns.register("tier" + tier, ExtendedCraftingRecipeSchema.tierSchema(tier));
        }

        // 3. Full grid compressor: event.recipes.exponential_mana.full_grid(result, tier, ingredient)
        ns.register("full_grid", ExtendedCraftingRecipeSchema.FULL_GRID_SCHEMA);

        // 4. Shapeless table crafting: event.recipes.exponential_mana.shapeless(result, tier, [ingredients])
        ns.register("shapeless", ExtendedCraftingRecipeSchema.SHAPELESS_SCHEMA);
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        // Expose items, blocks, and API helpers to JavaScript global context
        event.add("ExponentialMana", ExponentialManaApiWrapper.INSTANCE);
        event.add("EMItems", ModItems.class);
        event.add("EMBlocks", ModBlocks.class);
    }

    /**
     * API wrapper class callable directly from KubeJS server/startup scripts:
     *   ExponentialMana.registerCatalyst('my_item', 'exponential_mana:tier5_mana')
     *   ExponentialMana.registerOre('my_ore', 'my_inactive_ore', 3)
     */
    public static class ExponentialManaApiWrapper {
        public static final ExponentialManaApiWrapper INSTANCE = new ExponentialManaApiWrapper();

        public void registerCatalyst(Object catalystItem, Object manaItem) {
            FairyBlockEntity.registerCustomCatalyst(catalystItem, manaItem);
        }

        public void registerOre(Object activeOre, Object inactiveOre, int tier) {
            BatchOreBreakerBlockEntity.registerCustomOre(activeOre, inactiveOre, tier);
        }
    }
}
