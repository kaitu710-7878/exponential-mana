package com.exponentialmana.mod.recipe;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.registry.ModRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data-Driven Recipe Manager for Extended Crafting Tables (Tier 1..10).
 * Replaces hardcoded Java definitions with Minecraft's standard JSON-driven Recipe system.
 * Handles server-side and client-side recipe caching, high-speed matching, and automated crafting indexing.
 */
@Mod.EventBusSubscriber(modid = ExponentialMana.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExtendedCraftingRecipeManager {
    private static final Map<Integer, List<ExtendedCraftingRecipe>> RECIPES_BY_TIER = new ConcurrentHashMap<>();
    private static final List<ExtendedCraftingRecipe> ALL_RECIPES = new ArrayList<>();
    private static volatile boolean initialized = false;

    // Cache for automated crafting material matching: (Tier + Materials Hash) -> Recipe
    private static final Map<Long, ExtendedCraftingRecipe> AUTO_CRAFT_CACHE = new ConcurrentHashMap<>();

    static {
        for (int t = 1; t <= 10; t++) {
            RECIPES_BY_TIER.put(t, new ArrayList<>());
        }
    }

    /**
     * Updates recipes from Minecraft's RecipeManager.
     */
    public static synchronized void updateRecipes(RecipeManager recipeManager) {
        if (recipeManager == null) return;

        List<ExtendedCraftingRecipe> loaded = recipeManager.getAllRecipesFor(ModRecipeTypes.EXTENDED_CRAFTING.get());
        clearAndLoad(loaded);
    }

    public static synchronized void clearAndLoad(List<ExtendedCraftingRecipe> recipes) {
        RECIPES_BY_TIER.values().forEach(List::clear);
        ALL_RECIPES.clear();
        AUTO_CRAFT_CACHE.clear();

        for (ExtendedCraftingRecipe recipe : recipes) {
            int tier = recipe.getTier();
            if (tier >= 1 && tier <= 10) {
                RECIPES_BY_TIER.computeIfAbsent(tier, k -> new ArrayList<>()).add(recipe);
                ALL_RECIPES.add(recipe);
            }
        }
        initialized = true;
        ExponentialMana.LOGGER.info("Loaded {} Extended Crafting recipes from data JSONs.", ALL_RECIPES.size());
    }

    /**
     * Server-side reload listener event.
     */
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        // Clear cache upon reload
        AUTO_CRAFT_CACHE.clear();
    }

    /**
     * Server started event: inject recipes from server RecipeManager.
     */
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        updateRecipes(event.getServer().getRecipeManager());
    }

    /**
     * Client-side recipes updated event (sync from server).
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientRecipesUpdated(RecipesUpdatedEvent event) {
        updateRecipes(event.getRecipeManager());
    }

    /**
     * Ensures recipes are loaded for the given level.
     */
    public static void ensureLoaded(Level level) {
        if (!initialized && level != null) {
            updateRecipes(level.getRecipeManager());
        }
    }

    /**
     * Fallback for client if called before event or in JEI.
     */
    public static void ensureLoadedClient() {
        if (!initialized) {
            try {
                if (Minecraft.getInstance().level != null) {
                    updateRecipes(Minecraft.getInstance().level.getRecipeManager());
                }
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Returns an unmodifiable list of recipes for a specific tier.
     */
    public static List<ExtendedCraftingRecipe> getRecipesForTier(int tier) {
        ensureLoadedClient();
        List<ExtendedCraftingRecipe> list = RECIPES_BY_TIER.get(tier);
        return list != null ? Collections.unmodifiableList(list) : Collections.emptyList();
    }

    /**
     * Finds a recipe matching the grid items in an Extended Crafting Table.
     */
    public static ExtendedCraftingRecipe findMatchingRecipe(int tier, IItemHandler grid) {
        List<ExtendedCraftingRecipe> list = RECIPES_BY_TIER.get(tier);
        if (list == null || list.isEmpty()) return null;

        for (ExtendedCraftingRecipe recipe : list) {
            if (recipe.matches(grid)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Finds a craftable recipe based on available materials in the Material Buffer.
     * Uses a fast hash cache to eliminate redundant matching on unchanged buffers.
     */
    public static ExtendedCraftingRecipe findMatchingRecipeFromMaterials(int tier, Map<Item, Long> availableMaterials) {
        if (availableMaterials == null || availableMaterials.isEmpty()) return null;

        List<ExtendedCraftingRecipe> list = RECIPES_BY_TIER.get(tier);
        if (list == null || list.isEmpty()) return null;

        // Compute fast hash for available materials
        long hash = computeMaterialHash(tier, availableMaterials);
        ExtendedCraftingRecipe cached = AUTO_CRAFT_CACHE.get(hash);
        if (cached != null) {
            return cached;
        }

        for (ExtendedCraftingRecipe recipe : list) {
            Map<Item, Integer> required = recipe.getRequiredIngredients();
            if (required.isEmpty()) continue;

            boolean canCraft = true;
            for (Map.Entry<Item, Integer> req : required.entrySet()) {
                long avail = availableMaterials.getOrDefault(req.getKey(), 0L);
                if (avail < req.getValue()) {
                    canCraft = false;
                    break;
                }
            }
            if (canCraft) {
                if (AUTO_CRAFT_CACHE.size() > 500) {
                    AUTO_CRAFT_CACHE.clear(); // Prevent memory bloat
                }
                AUTO_CRAFT_CACHE.put(hash, recipe);
                return recipe;
            }
        }

        return null;
    }

    private static long computeMaterialHash(int tier, Map<Item, Long> map) {
        long h = tier * 31L;
        for (Map.Entry<Item, Long> e : map.entrySet()) {
            h = h * 37L + System.identityHashCode(e.getKey()) ^ e.getValue();
        }
        return h;
    }
}
