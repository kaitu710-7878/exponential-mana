package com.exponentialmana.mod.recipe;

import com.exponentialmana.mod.registry.ModBlocks;
import com.exponentialmana.mod.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

/**
 * Registry and matching engine for Extended Crafting Tables recipes.
 * Strictly compliant with 'Exponential Mana' Design Specification (Part 2).
 */
public class ExtendedCraftingRecipeManager {
    private static final Map<Integer, List<ExtendedCraftingRecipe>> RECIPES_BY_TIER = new HashMap<>();
    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) return;
        RECIPES_BY_TIER.clear();
        for (int i = 1; i <= 10; i++) {
            RECIPES_BY_TIER.put(i, new ArrayList<>());
        }

        populateRecipes(RECIPES_BY_TIER);
        initialized = true;
    }

    public static List<ExtendedCraftingRecipe> getRecipesForTier(int tier) {
        init();
        return RECIPES_BY_TIER.getOrDefault(tier, Collections.emptyList());
    }

    public static ExtendedCraftingRecipe findMatchingRecipe(int tier, IItemHandler grid) {
        init();
        List<ExtendedCraftingRecipe> list = RECIPES_BY_TIER.get(tier);
        if (list == null) return null;

        for (ExtendedCraftingRecipe recipe : list) {
            if (recipe.matches(grid)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Finds a recipe for the given tier whose ingredient requirements are fully satisfied
     * by the provided pool of available materials (e.g. from material buffer and grid).
     */
    public static ExtendedCraftingRecipe findMatchingRecipeFromMaterials(int tier, Map<net.minecraft.world.item.Item, Long> availableMaterials) {
        init();
        List<ExtendedCraftingRecipe> list = RECIPES_BY_TIER.get(tier);
        if (list == null) return null;

        for (ExtendedCraftingRecipe recipe : list) {
            Map<net.minecraft.world.item.Item, Integer> req = recipe.getRequiredIngredients();
            if (req.isEmpty()) continue;
            boolean canCraft = true;
            for (Map.Entry<net.minecraft.world.item.Item, Integer> entry : req.entrySet()) {
                if (availableMaterials.getOrDefault(entry.getKey(), 0L) < entry.getValue()) {
                    canCraft = false;
                    break;
                }
            }
            if (canCraft) {
                return recipe;
            }
        }
        return null;
    }

    private static void populateRecipes(Map<Integer, List<ExtendedCraftingRecipe>> map) {
        // ==========================================
        // Tier 1 Extended Crafting Table (4x4 Grid - 16 slots)
        // Crafts: Tier 2 items (Copper)
        // ==========================================
        // Tier 2 Mana: Tier 1 Mana x12 (outer) + Copper Ingot x4 (center 2x2)
        map.get(1).add(createCenterBoxRecipe(1, 4, 2,
                new ItemStack(ModItems.TIER1_MANA.get()), new ItemStack(Items.COPPER_INGOT),
                new ItemStack(ModItems.TIER2_MANA.get()), Component.translatable("item.exponential_mana.tier2_mana")));

        // Tier 2 Core: Tier 2 Mana x12 (outer) + Copper Ingot x4 (center 2x2)
        map.get(1).add(createCenterBoxRecipe(1, 4, 2,
                new ItemStack(ModItems.TIER2_MANA.get()), new ItemStack(Items.COPPER_INGOT),
                new ItemStack(ModItems.TIER2_CORE.get()), Component.translatable("item.exponential_mana.tier2_core")));

        // Tier 2 Mana Ore: Tier 1 Ore Shard x4 (center 2x2) + Tier 2 Core x12 (outer)
        map.get(1).add(createCenterBoxRecipe(1, 4, 2,
                new ItemStack(ModItems.TIER2_CORE.get()), new ItemStack(ModItems.TIER1_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER2_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier2_mana_ore")));

        // Tier 2 Fairy Catalyst: Tier 1 Catalyst Shard x4 (center 2x2) + Tier 2 Core x12 (outer)
        map.get(1).add(createCenterBoxRecipe(1, 4, 2,
                new ItemStack(ModItems.TIER2_CORE.get()), new ItemStack(ModItems.TIER1_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER2_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier2_fairy_catalyst")));

        // Tier 2 Extended Crafting Table (5x5): Tier 1 Mini Table x4 (center 2x2) + Tier 2 Core x12 (outer)
        map.get(1).add(createCenterBoxRecipe(1, 4, 2,
                new ItemStack(ModItems.TIER2_CORE.get()), new ItemStack(ModItems.TIER1_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER2_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier2_extended_crafting_table")));

        // ==========================================
        // Tier 2 Extended Crafting Table (5x5 Grid - 25 slots)
        // Crafts: Tier 3 items (Redstone)
        // ==========================================
        // Tier 3 Mana: Tier 2 Mana x24 (outer) + Redstone x1 (center 1x1)
        map.get(2).add(createCenterBoxRecipe(2, 5, 1,
                new ItemStack(ModItems.TIER2_MANA.get()), new ItemStack(Items.REDSTONE),
                new ItemStack(ModItems.TIER3_MANA.get()), Component.translatable("item.exponential_mana.tier3_mana")));

        // Tier 3 Core: Tier 3 Mana x16 (outer) + Redstone x9 (center 3x3)
        map.get(2).add(createCenterBoxRecipe(2, 5, 3,
                new ItemStack(ModItems.TIER3_MANA.get()), new ItemStack(Items.REDSTONE),
                new ItemStack(ModItems.TIER3_CORE.get()), Component.translatable("item.exponential_mana.tier3_core")));

        // Tier 3 Mana Ore: Tier 3 Core x24 (outer) + Tier 2 Mana Ore x1 (center 1x1)
        map.get(2).add(createCenterBoxRecipe(2, 5, 1,
                new ItemStack(ModItems.TIER3_CORE.get()), new ItemStack(ModBlocks.TIER2_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER3_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier3_mana_ore")));

        // Tier 3 Fairy Catalyst: Tier 3 Core x24 (outer) + Tier 2 Fairy Catalyst x1 (center 1x1)
        map.get(2).add(createCenterBoxRecipe(2, 5, 1,
                new ItemStack(ModItems.TIER3_CORE.get()), new ItemStack(ModItems.TIER2_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER3_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier3_fairy_catalyst")));

        // Tier 3 Extended Crafting Table (6x6): Tier 3 Core x24 (outer) + Tier 2 Table x1 (center 1x1)
        map.get(2).add(createCenterBoxRecipe(2, 5, 1,
                new ItemStack(ModItems.TIER3_CORE.get()), new ItemStack(ModBlocks.TIER2_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER3_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier3_extended_crafting_table")));

        // ==========================================
        // Tier 3 Extended Crafting Table (6x6 Grid - 36 slots)
        // Crafts: Tier 4 items (Glowstone)
        // ==========================================
        // Tier 4 Mana: Tier 3 Mana x32 (outer) + Glowstone Dust x4 (center 2x2)
        map.get(3).add(createCenterBoxRecipe(3, 6, 2,
                new ItemStack(ModItems.TIER3_MANA.get()), new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(ModItems.TIER4_MANA.get()), Component.translatable("item.exponential_mana.tier4_mana")));

        // Tier 4 Core: Tier 4 Mana x32 (outer) + Glowstone Dust x4 (center 2x2)
        map.get(3).add(createCenterBoxRecipe(3, 6, 2,
                new ItemStack(ModItems.TIER4_MANA.get()), new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(ModItems.TIER4_CORE.get()), Component.translatable("item.exponential_mana.tier4_core")));

        // Tier 4 Mana Ore: Tier 3 Ore Shard x4 (center 2x2) + Tier 4 Core x32 (outer)
        map.get(3).add(createCenterBoxRecipe(3, 6, 2,
                new ItemStack(ModItems.TIER4_CORE.get()), new ItemStack(ModItems.TIER3_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER4_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier4_mana_ore")));

        // Tier 4 Fairy Catalyst: Tier 3 Catalyst Shard x4 (center 2x2) + Tier 4 Core x32 (outer)
        map.get(3).add(createCenterBoxRecipe(3, 6, 2,
                new ItemStack(ModItems.TIER4_CORE.get()), new ItemStack(ModItems.TIER3_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER4_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier4_fairy_catalyst")));

        // Tier 4 Extended Crafting Table (7x7): Tier 3 Mini Table x4 (center 2x2) + Tier 4 Core x32 (outer)
        map.get(3).add(createCenterBoxRecipe(3, 6, 2,
                new ItemStack(ModItems.TIER4_CORE.get()), new ItemStack(ModItems.TIER3_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER4_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier4_extended_crafting_table")));

        // ==========================================
        // Tier 4 Extended Crafting Table (7x7 Grid - 49 slots)
        // Crafts: Tier 5 items (Iron)
        // ==========================================
        // Tier 5 Mana: Tier 4 Mana x48 (outer) + Iron Ingot x1 (center 1x1)
        map.get(4).add(createCenterBoxRecipe(4, 7, 1,
                new ItemStack(ModItems.TIER4_MANA.get()), new ItemStack(Items.IRON_INGOT),
                new ItemStack(ModItems.TIER5_MANA.get()), Component.translatable("item.exponential_mana.tier5_mana")));

        // Tier 5 Core: Tier 5 Mana x40 (outer) + Iron Ingot x9 (center 3x3)
        map.get(4).add(createCenterBoxRecipe(4, 7, 3,
                new ItemStack(ModItems.TIER5_MANA.get()), new ItemStack(Items.IRON_INGOT),
                new ItemStack(ModItems.TIER5_CORE.get()), Component.translatable("item.exponential_mana.tier5_core")));

        // Tier 5 Mana Ore: Tier 5 Core x48 (outer) + Tier 4 Mana Ore x1 (center 1x1)
        map.get(4).add(createCenterBoxRecipe(4, 7, 1,
                new ItemStack(ModItems.TIER5_CORE.get()), new ItemStack(ModBlocks.TIER4_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER5_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier5_mana_ore")));

        // Tier 5 Fairy Catalyst: Tier 5 Core x48 (outer) + Tier 4 Fairy Catalyst x1 (center 1x1)
        map.get(4).add(createCenterBoxRecipe(4, 7, 1,
                new ItemStack(ModItems.TIER5_CORE.get()), new ItemStack(ModItems.TIER4_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER5_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier5_fairy_catalyst")));

        // Tier 5 Extended Crafting Table (8x8): Tier 5 Core x48 (outer) + Tier 4 Table x1 (center 1x1)
        map.get(4).add(createCenterBoxRecipe(4, 7, 1,
                new ItemStack(ModItems.TIER5_CORE.get()), new ItemStack(ModBlocks.TIER4_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER5_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier5_extended_crafting_table")));

        // ==========================================
        // Tier 5 Extended Crafting Table (8x8 Grid - 64 slots)
        // Crafts: Tier 6 items (Gold)
        // ==========================================
        // Tier 6 Mana: Tier 5 Mana x60 (outer) + Gold Ingot x4 (center 2x2)
        map.get(5).add(createCenterBoxRecipe(5, 8, 2,
                new ItemStack(ModItems.TIER5_MANA.get()), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ModItems.TIER6_MANA.get()), Component.translatable("item.exponential_mana.tier6_mana")));

        // Tier 6 Core: Tier 6 Mana x48 (outer) + Gold Ingot x16 (center 4x4)
        map.get(5).add(createCenterBoxRecipe(5, 8, 4,
                new ItemStack(ModItems.TIER6_MANA.get()), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ModItems.TIER6_CORE.get()), Component.translatable("item.exponential_mana.tier6_core")));

        // Tier 6 Mana Ore: Tier 5 Ore Shard x4 (center 2x2) + Tier 6 Core x60 (outer)
        map.get(5).add(createCenterBoxRecipe(5, 8, 2,
                new ItemStack(ModItems.TIER6_CORE.get()), new ItemStack(ModItems.TIER5_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER6_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier6_mana_ore")));

        // Tier 6 Fairy Catalyst: Tier 5 Catalyst Shard x4 (center 2x2) + Tier 6 Core x60 (outer)
        map.get(5).add(createCenterBoxRecipe(5, 8, 2,
                new ItemStack(ModItems.TIER6_CORE.get()), new ItemStack(ModItems.TIER5_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER6_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier6_fairy_catalyst")));

        // Tier 6 Extended Crafting Table (9x9): Tier 5 Mini Table x4 (center 2x2) + Tier 6 Core x60 (outer)
        map.get(5).add(createCenterBoxRecipe(5, 8, 2,
                new ItemStack(ModItems.TIER6_CORE.get()), new ItemStack(ModItems.TIER5_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER6_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier6_extended_crafting_table")));

        // ==========================================
        // Tier 6 Extended Crafting Table (9x9 Grid - 81 slots)
        // Crafts: Tier 7 items (Diamond)
        // ==========================================
        // Tier 7 Mana: Tier 6 Mana x80 (outer) + Diamond x1 (center 1x1)
        map.get(6).add(createCenterBoxRecipe(6, 9, 1,
                new ItemStack(ModItems.TIER6_MANA.get()), new ItemStack(Items.DIAMOND),
                new ItemStack(ModItems.TIER7_MANA.get()), Component.translatable("item.exponential_mana.tier7_mana")));

        // Tier 7 Core: Tier 7 Mana x72 (outer) + Diamond x9 (center 3x3)
        map.get(6).add(createCenterBoxRecipe(6, 9, 3,
                new ItemStack(ModItems.TIER7_MANA.get()), new ItemStack(Items.DIAMOND),
                new ItemStack(ModItems.TIER7_CORE.get()), Component.translatable("item.exponential_mana.tier7_core")));

        // Tier 7 Mana Ore: Tier 7 Core x80 (outer) + Tier 6 Mana Ore x1 (center 1x1)
        map.get(6).add(createCenterBoxRecipe(6, 9, 1,
                new ItemStack(ModItems.TIER7_CORE.get()), new ItemStack(ModBlocks.TIER6_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER7_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier7_mana_ore")));

        // Tier 7 Fairy Catalyst: Tier 7 Core x80 (outer) + Tier 6 Fairy Catalyst x1 (center 1x1)
        map.get(6).add(createCenterBoxRecipe(6, 9, 1,
                new ItemStack(ModItems.TIER7_CORE.get()), new ItemStack(ModItems.TIER6_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER7_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier7_fairy_catalyst")));

        // Tier 7 Extended Crafting Table (10x10): Tier 7 Core x80 (outer) + Tier 6 Table x1 (center 1x1)
        map.get(6).add(createCenterBoxRecipe(6, 9, 1,
                new ItemStack(ModItems.TIER7_CORE.get()), new ItemStack(ModBlocks.TIER6_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER7_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier7_extended_crafting_table")));

        // ==========================================
        // Tier 7 Extended Crafting Table (10x10 Grid - 100 slots)
        // Crafts: Tier 8 items (Obsidian)
        // ==========================================
        // Tier 8 Mana: Tier 7 Mana x96 (outer) + Obsidian x4 (center 2x2)
        map.get(7).add(createCenterBoxRecipe(7, 10, 2,
                new ItemStack(ModItems.TIER7_MANA.get()), new ItemStack(Items.OBSIDIAN),
                new ItemStack(ModItems.TIER8_MANA.get()), Component.translatable("item.exponential_mana.tier8_mana")));

        // Tier 8 Core: Tier 8 Mana x64 (outer) + Obsidian x36 (center 6x6)
        map.get(7).add(createCenterBoxRecipe(7, 10, 6,
                new ItemStack(ModItems.TIER8_MANA.get()), new ItemStack(Items.OBSIDIAN),
                new ItemStack(ModItems.TIER8_CORE.get()), Component.translatable("item.exponential_mana.tier8_core")));

        // Tier 8 Mana Ore: Tier 7 Ore Shard x4 (center 2x2) + Tier 8 Core x96 (outer)
        map.get(7).add(createCenterBoxRecipe(7, 10, 2,
                new ItemStack(ModItems.TIER8_CORE.get()), new ItemStack(ModItems.TIER7_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER8_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier8_mana_ore")));

        // Tier 8 Fairy Catalyst: Tier 7 Catalyst Shard x4 (center 2x2) + Tier 8 Core x96 (outer)
        map.get(7).add(createCenterBoxRecipe(7, 10, 2,
                new ItemStack(ModItems.TIER8_CORE.get()), new ItemStack(ModItems.TIER7_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER8_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier8_fairy_catalyst")));

        // Tier 8 Extended Crafting Table (11x11): Tier 7 Mini Table x4 (center 2x2) + Tier 8 Core x96 (outer)
        map.get(7).add(createCenterBoxRecipe(7, 10, 2,
                new ItemStack(ModItems.TIER8_CORE.get()), new ItemStack(ModItems.TIER7_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER8_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier8_extended_crafting_table")));

        // ==========================================
        // Tier 8 Extended Crafting Table (11x11 Grid - 121 slots)
        // Crafts: Tier 9 items (Emerald)
        // ==========================================
        // Tier 9 Mana: Tier 8 Mana x112 (outer) + Emerald x9 (center 3x3)
        map.get(8).add(createCenterBoxRecipe(8, 11, 3,
                new ItemStack(ModItems.TIER8_MANA.get()), new ItemStack(Items.EMERALD),
                new ItemStack(ModItems.TIER9_MANA.get()), Component.translatable("item.exponential_mana.tier9_mana")));

        // Tier 9 Core: Tier 9 Mana x112 (outer) + Emerald x9 (center 3x3)
        map.get(8).add(createCenterBoxRecipe(8, 11, 3,
                new ItemStack(ModItems.TIER9_MANA.get()), new ItemStack(Items.EMERALD),
                new ItemStack(ModItems.TIER9_CORE.get()), Component.translatable("item.exponential_mana.tier9_core")));

        // Tier 9 Mana Ore: Tier 9 Core x120 (outer) + Tier 8 Mana Ore x1 (center 1x1)
        map.get(8).add(createCenterBoxRecipe(8, 11, 1,
                new ItemStack(ModItems.TIER9_CORE.get()), new ItemStack(ModBlocks.TIER8_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER9_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier9_mana_ore")));

        // Tier 9 Fairy Catalyst: Tier 9 Core x120 (outer) + Tier 8 Fairy Catalyst x1 (center 1x1)
        map.get(8).add(createCenterBoxRecipe(8, 11, 1,
                new ItemStack(ModItems.TIER9_CORE.get()), new ItemStack(ModItems.TIER8_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER9_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier9_fairy_catalyst")));

        // Tier 9 Extended Crafting Table (12x12): Tier 9 Core x120 (outer) + Tier 8 Table x1 (center 1x1)
        map.get(8).add(createCenterBoxRecipe(8, 11, 1,
                new ItemStack(ModItems.TIER9_CORE.get()), new ItemStack(ModBlocks.TIER8_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER9_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier9_extended_crafting_table")));

        // ==========================================
        // Tier 9 Extended Crafting Table (12x12 Grid - 144 slots)
        // Crafts: Tier 10 items (Netherite)
        // ==========================================
        // Tier 10 Mana: Tier 9 Mana x128 (outer) + Netherite Ingot x16 (center 4x4)
        map.get(9).add(createCenterBoxRecipe(9, 12, 4,
                new ItemStack(ModItems.TIER9_MANA.get()), new ItemStack(Items.NETHERITE_INGOT),
                new ItemStack(ModItems.TIER10_MANA.get()), Component.translatable("item.exponential_mana.tier10_mana")));

        // Tier 10 Core: Tier 10 Mana x128 (outer) + Netherite Ingot x16 (center 4x4)
        map.get(9).add(createCenterBoxRecipe(9, 12, 4,
                new ItemStack(ModItems.TIER10_MANA.get()), new ItemStack(Items.NETHERITE_INGOT),
                new ItemStack(ModItems.TIER10_CORE.get()), Component.translatable("item.exponential_mana.tier10_core")));

        // Tier 10 Mana Ore: Tier 9 Ore Shard x4 (center 2x2) + Tier 10 Core x140 (outer)
        map.get(9).add(createCenterBoxRecipe(9, 12, 2,
                new ItemStack(ModItems.TIER10_CORE.get()), new ItemStack(ModItems.TIER9_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER10_MANA_ORE.get()), Component.translatable("block.exponential_mana.tier10_mana_ore")));

        // Tier 10 Fairy Catalyst: Tier 9 Catalyst Shard x4 (center 2x2) + Tier 10 Core x140 (outer)
        map.get(9).add(createCenterBoxRecipe(9, 12, 2,
                new ItemStack(ModItems.TIER10_CORE.get()), new ItemStack(ModItems.TIER9_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER10_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.tier10_fairy_catalyst")));

        // Tier 10 Extended Crafting Table (13x13): Tier 9 Mini Table x4 (center 2x2) + Tier 10 Core x140 (outer)
        map.get(9).add(createCenterBoxRecipe(9, 12, 2,
                new ItemStack(ModItems.TIER10_CORE.get()), new ItemStack(ModItems.TIER9_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER10_EXTENDED_CRAFTING_TABLE.get()), Component.translatable("block.exponential_mana.tier10_extended_crafting_table")));

        // ==========================================
        // Tier 10 Extended Crafting Table (13x13 Grid - 169 slots)
        // End-Game Recipes
        // ==========================================

        // 1. Mana Symbols (Tier 0 to Tier 10) - 169 mana per symbol
        ItemStack[] allMana = new ItemStack[]{
                new ItemStack(ModItems.TIER0_MANA.get()), new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(ModItems.TIER2_MANA.get()), new ItemStack(ModItems.TIER3_MANA.get()),
                new ItemStack(ModItems.TIER4_MANA.get()), new ItemStack(ModItems.TIER5_MANA.get()),
                new ItemStack(ModItems.TIER6_MANA.get()), new ItemStack(ModItems.TIER7_MANA.get()),
                new ItemStack(ModItems.TIER8_MANA.get()), new ItemStack(ModItems.TIER9_MANA.get()),
                new ItemStack(ModItems.TIER10_MANA.get())
        };
        ItemStack[] allSymbols = new ItemStack[]{
                new ItemStack(ModItems.MANA_SYMBOL_TIER0.get()), new ItemStack(ModItems.MANA_SYMBOL_TIER1.get()),
                new ItemStack(ModItems.MANA_SYMBOL_TIER2.get()), new ItemStack(ModItems.MANA_SYMBOL_TIER3.get()),
                new ItemStack(ModItems.MANA_SYMBOL_TIER4.get()), new ItemStack(ModItems.MANA_SYMBOL_TIER5.get()),
                new ItemStack(ModItems.MANA_SYMBOL_TIER6.get()), new ItemStack(ModItems.MANA_SYMBOL_TIER7.get()),
                new ItemStack(ModItems.MANA_SYMBOL_TIER8.get()), new ItemStack(ModItems.MANA_SYMBOL_TIER9.get()),
                new ItemStack(ModItems.MANA_SYMBOL_TIER10.get())
        };
        ItemStack[] allCompressedMana = new ItemStack[]{
                new ItemStack(ModItems.COMPRESSED_TIER0_MANA.get()), new ItemStack(ModItems.COMPRESSED_TIER1_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER2_MANA.get()), new ItemStack(ModItems.COMPRESSED_TIER3_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER4_MANA.get()), new ItemStack(ModItems.COMPRESSED_TIER5_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER6_MANA.get()), new ItemStack(ModItems.COMPRESSED_TIER7_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER8_MANA.get()), new ItemStack(ModItems.COMPRESSED_TIER9_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER10_MANA.get())
        };

        // Symbols recipes (169 Tier N mana -> Mana Symbol Tier N)
        for (int t = 0; t <= 10; t++) {
            map.get(10).add(createFullGridRecipe(10, 13, allMana[t], allSymbols[t],
                    Component.translatable("item.exponential_mana.mana_symbol_tier" + t)));
        }

        // Compressed Mana recipes from Symbols:
        // Tier 0: 169 (Tier 10 table 13x13 full, shapeless)
        map.get(10).add(createFullGridRecipe(10, 13, allSymbols[0], allCompressedMana[0],
                Component.translatable("item.exponential_mana.compressed_tier0_mana")));

        // Tier 1 to 10: native matching tier table (Tier 1: 4x4, Tier 2: 5x5, ..., Tier 10: 13x13 full, shapeless)
        for (int t = 1; t <= 10; t++) {
            int tableTier = t;
            int dim = t + 3; // Tier 1: 4, Tier 2: 5, ..., Tier 10: 13
            map.get(tableTier).add(createFullGridRecipe(tableTier, dim, allSymbols[t], allCompressedMana[t],
                    Component.translatable("item.exponential_mana.compressed_tier" + t + "_mana")));
        }

        // 2. Pure Mana & Pure Blocks
        // Pure Mana: Compressed Tier 0-10 Mana (11 types) x15 each = 165 slots (corners empty)
        map.get(10).add(createPureManaRecipe(new ItemStack(ModItems.PURE_MANA.get()),
                Component.translatable("item.exponential_mana.pure_mana")));

        // Pure Mana Ore: Pure Mana x168 (outer) + Tier 10 Mana Ore x1 (center 1x1)
        map.get(10).add(createCenterBoxRecipe(10, 13, 1,
                new ItemStack(ModItems.PURE_MANA.get()), new ItemStack(ModBlocks.TIER10_MANA_ORE.get()),
                new ItemStack(ModBlocks.PURE_MANA_ORE.get()), Component.translatable("block.exponential_mana.pure_mana_ore")));

        // Pure Fairy Catalyst: Pure Mana x168 (outer) + Tier 10 Fairy Catalyst x1 (center 1x1)
        map.get(10).add(createCenterBoxRecipe(10, 13, 1,
                new ItemStack(ModItems.PURE_MANA.get()), new ItemStack(ModItems.TIER10_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.PURE_FAIRY_CATALYST.get()), Component.translatable("item.exponential_mana.pure_fairy_catalyst")));

        // 3. Mana Materials & Compressed Materials
        // Tier 1 to 10 Mana Materials & Compressed Mana Materials
        // Placed on native matching tier tables (Tier 1: 4x4 up to Tier 10: 13x13)
        ItemStack[] vanillaMaterials = new ItemStack[]{
                new ItemStack(Items.COAL),
                new ItemStack(Items.COPPER_INGOT),
                new ItemStack(Items.REDSTONE),
                new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DIAMOND),
                new ItemStack(Items.OBSIDIAN),
                new ItemStack(Items.EMERALD),
                new ItemStack(Items.NETHERITE_INGOT)
        };
        ItemStack[] manaMaterials = new ItemStack[]{
                new ItemStack(ModItems.TIER1_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER2_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER3_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER4_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER5_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER6_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER7_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER8_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER9_MANA_MATERIAL.get()),
                new ItemStack(ModItems.TIER10_MANA_MATERIAL.get())
        };
        ItemStack[] compManaMaterials = new ItemStack[]{
                new ItemStack(ModItems.COMPRESSED_TIER1_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER2_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER3_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER4_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER5_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER6_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER7_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER8_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER9_MANA_MATERIAL.get()),
                new ItemStack(ModItems.COMPRESSED_TIER10_MANA_MATERIAL.get())
        };

        for (int i = 0; i < 10; i++) {
            int tier = i + 1; // Tier 1 to 10
            int tableTier = tier;
            int gridSize = tier + 3; // Tier 1: 4, Tier 2: 5, ..., Tier 10: 13
            int centerBox = (gridSize >= 10) ? 4 : (gridSize >= 6 ? 2 : 1);

            // Mana Material: Mana (outer) + Vanilla Material (center)
            map.get(tableTier).add(createCenterBoxRecipe(tableTier, gridSize, centerBox,
                    allMana[tier], vanillaMaterials[i], manaMaterials[i],
                    Component.translatable("item.exponential_mana.tier" + tier + "_mana_material")));

            // Compressed Mana Material: Full grid compression (shapeless)
            map.get(tableTier).add(createFullGridRecipe(tableTier, gridSize,
                    manaMaterials[i], compManaMaterials[i],
                    Component.translatable("item.exponential_mana.compressed_tier" + tier + "_mana_material")));
        }

        // Compressed Minerals (Lapis, Amethyst, Quartz x169)
        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(Items.LAPIS_LAZULI), new ItemStack(ModItems.COMPRESSED_LAPIS.get()),
                Component.translatable("item.exponential_mana.compressed_lapis")));

        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(Items.AMETHYST_SHARD), new ItemStack(ModItems.COMPRESSED_AMETHYST.get()),
                Component.translatable("item.exponential_mana.compressed_amethyst")));

        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(Items.QUARTZ), new ItemStack(ModItems.COMPRESSED_QUARTZ.get()),
                Component.translatable("item.exponential_mana.compressed_quartz")));

        // Origin Alloy: 10 Compressed Mana Materials x12 each (120) + 3 Compressed Minerals x12 each (36) + Pure Mana x13 = 169
        map.get(10).add(createOriginAlloyRecipe(new ItemStack(ModItems.ORIGIN_ALLOY.get()),
                Component.translatable("item.exponential_mana.origin_alloy")));

        // Compressed Origin Alloy: Origin Alloy x169
        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.ORIGIN_ALLOY.get()), new ItemStack(ModItems.COMPRESSED_ORIGIN_ALLOY.get()),
                Component.translatable("item.exponential_mana.compressed_origin_alloy")));

        // 4. World Wisdom & 4 Root Manas
        // Mana Knowledge: 13x13 recipe requiring all tiers (0-10) of Mana, Ore, and Catalyst (T0 fairy)
        map.get(10).add(createManaKnowledgeRecipe());

        // Mana Wisdom: Mana Knowledge x169
        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.MANA_KNOWLEDGE.get()), new ItemStack(ModItems.MANA_WISDOM.get()),
                Component.translatable("item.exponential_mana.mana_wisdom")));

        // Star Mana: Nether Star x1 (center) + Pure Mana x168 (outer) -> Uncompressed -> x169 -> Star Mana
        map.get(10).add(createCenterBoxRecipe(10, 13, 1,
                new ItemStack(ModItems.PURE_MANA.get()), new ItemStack(Items.NETHER_STAR),
                new ItemStack(ModItems.UNCOMPRESSED_STAR_MANA.get()), Component.translatable("item.exponential_mana.uncompressed_star_mana")));
        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_STAR_MANA.get()), new ItemStack(ModItems.STAR_MANA.get()),
                Component.translatable("item.exponential_mana.star_mana")));

        // Quantum Mana: End Crystal x1 (center) + Pure Mana x168 (outer) -> Uncompressed -> x169 -> Quantum Mana
        map.get(10).add(createCenterBoxRecipe(10, 13, 1,
                new ItemStack(ModItems.PURE_MANA.get()), new ItemStack(Items.END_CRYSTAL),
                new ItemStack(ModItems.UNCOMPRESSED_QUANTUM_MANA.get()), Component.translatable("item.exponential_mana.uncompressed_quantum_mana")));
        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_QUANTUM_MANA.get()), new ItemStack(ModItems.QUANTUM_MANA.get()),
                Component.translatable("item.exponential_mana.quantum_mana")));

        // Abyssal Mana: Echo Shard x1 (center) + Pure Mana x168 (outer) -> Uncompressed -> x169 -> Abyssal Mana
        map.get(10).add(createCenterBoxRecipe(10, 13, 1,
                new ItemStack(ModItems.PURE_MANA.get()), new ItemStack(Items.ECHO_SHARD),
                new ItemStack(ModItems.UNCOMPRESSED_ABYSSAL_MANA.get()), Component.translatable("item.exponential_mana.uncompressed_abyssal_mana")));
        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_ABYSSAL_MANA.get()), new ItemStack(ModItems.ABYSSAL_MANA.get()),
                Component.translatable("item.exponential_mana.abyssal_mana")));

        // Marine Mana: Activated Conduit x1 (center) + Pure Mana x168 (outer) -> Uncompressed -> x169 -> Marine Mana
        map.get(10).add(createCenterBoxRecipe(10, 13, 1,
                new ItemStack(ModItems.PURE_MANA.get()), new ItemStack(ModItems.ACTIVATED_CONDUIT.get()),
                new ItemStack(ModItems.UNCOMPRESSED_MARINE_MANA.get()), Component.translatable("item.exponential_mana.uncompressed_marine_mana")));
        map.get(10).add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_MARINE_MANA.get()), new ItemStack(ModItems.MARINE_MANA.get()),
                Component.translatable("item.exponential_mana.marine_mana")));

        // 5. Mana Root & Origin Equipment
        // Mana Root: Comp Origin Alloy x25 + Mana Wisdom x24 + Star Mana x30 + Quantum x30 + Abyssal x30 + Marine x30 = 169
        map.get(10).add(createManaRootRecipe(new ItemStack(ModItems.MANA_ROOT.get()),
                Component.translatable("item.exponential_mana.mana_root")));

        // Origin Equipment: Shape of Armor / Sword made of Mana Root
        map.get(10).add(createOriginSwordRecipe(new ItemStack(ModItems.ORIGIN_SWORD.get()),
                Component.translatable("item.exponential_mana.origin_sword")));
        map.get(10).add(createOriginHelmetRecipe(new ItemStack(ModItems.ORIGIN_HELMET.get()),
                Component.translatable("item.exponential_mana.origin_helmet")));
        map.get(10).add(createOriginChestplateRecipe(new ItemStack(ModItems.ORIGIN_CHESTPLATE.get()),
                Component.translatable("item.exponential_mana.origin_chestplate")));
        map.get(10).add(createOriginLeggingsRecipe(new ItemStack(ModItems.ORIGIN_LEGGINGS.get()),
                Component.translatable("item.exponential_mana.origin_leggings")));
        map.get(10).add(createOriginBootsRecipe(new ItemStack(ModItems.ORIGIN_BOOTS.get()),
                Component.translatable("item.exponential_mana.origin_boots")));

        // 6. Infinite Mana
        map.get(10).add(createInfiniteManaRecipe(new ItemStack(ModItems.INFINITE_MANA.get()),
                Component.translatable("item.exponential_mana.infinite_mana")));
    }

    /**
     * Creates a concentric box recipe where center boxSize x boxSize contains center, and outer border contains outer.
     */
    private static ExtendedCraftingRecipe createCenterBoxRecipe(int tier, int gridSize, int boxSize, ItemStack outer, ItemStack center, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        int start = (gridSize - boxSize) / 2;
        int end = start + boxSize;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean isCenter = (r >= start && r < end && c >= start && c < end);
                inputs.add(Collections.singletonList(isCenter ? center.copy() : outer.copy()));
            }
        }
        return new ExtendedCraftingRecipe(tier, gridSize, inputs, output, name);
    }

    /**
     * Creates a full-grid uniform compression recipe (shapeless, requires gridSize*gridSize of the fill item).
     */
    private static ExtendedCraftingRecipe createFullGridRecipe(int tier, int gridSize, ItemStack fill, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        for (int i = 0; i < gridSize * gridSize; i++) {
            inputs.add(Collections.singletonList(fill.copy()));
        }
        return new ExtendedCraftingRecipe(tier, gridSize, inputs, output, name, true);
    }

    /**
     * Creates a centered subgrid recipe in a larger grid (e.g. 12x12 inside 13x13).
     */
    private static ExtendedCraftingRecipe createCenteredSubGridRecipe(int tier, int gridSize, int subSize, ItemStack subItem, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        int start = (gridSize - subSize) / 2;
        int end = start + subSize;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean inside = (r >= start && r < end && c >= start && c < end);
                inputs.add(inside ? Collections.singletonList(subItem.copy()) : Collections.emptyList());
            }
        }
        return new ExtendedCraftingRecipe(tier, gridSize, inputs, output, name);
    }

    /**
     * Pure Mana: Compressed Tier 0 to 10 Mana (11 kinds), 15 each = 165 items placed in 13x13 (4 corner slots empty).
     */
    private static ExtendedCraftingRecipe createPureManaRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack[] compTier = new ItemStack[]{
                new ItemStack(ModItems.COMPRESSED_TIER0_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER1_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER2_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER3_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER4_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER5_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER6_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER7_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER8_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER9_MANA.get()),
                new ItemStack(ModItems.COMPRESSED_TIER10_MANA.get())
        };

        int count = 0;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean isCorner = (r == 0 && c == 0) || (r == 0 && c == 12) || (r == 12 && c == 0) || (r == 12 && c == 12);
                if (isCorner) {
                    inputs.add(Collections.emptyList());
                } else {
                    int kind = count % 11;
                    inputs.add(Collections.singletonList(compTier[kind].copy()));
                    count++;
                }
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Origin Alloy: 10 Compressed Mana Materials x12 (120) + 3 Compressed Minerals x12 (36) + Pure Mana x13 = 169.
     */
    private static ExtendedCraftingRecipe createOriginAlloyRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        List<ItemStack> items13 = new ArrayList<>();
        // 10 Compressed Mana Materials
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER1_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER2_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER3_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER4_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER5_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER6_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER7_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER8_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER9_MANA_MATERIAL.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_TIER10_MANA_MATERIAL.get()));
        // 3 Compressed Minerals
        items13.add(new ItemStack(ModItems.COMPRESSED_LAPIS.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_AMETHYST.get()));
        items13.add(new ItemStack(ModItems.COMPRESSED_QUARTZ.get()));

        ItemStack pureMana = new ItemStack(ModItems.PURE_MANA.get());

        // Fill: Each row 0..11 gets one of the 13 items in columns 0..11, and column 12 gets row's item or Pure Mana.
        // Elegant arrangement:
        // Center diagonal has 13 Pure Mana (r == c).
        // The remaining 156 slots (13 x 12) are evenly filled with the 13 materials (each appears exactly 12 times!).
        int fillIdx = 0;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (r == c) {
                    inputs.add(Collections.singletonList(pureMana.copy()));
                } else {
                    inputs.add(Collections.singletonList(items13.get(fillIdx % 13).copy()));
                    fillIdx++;
                }
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Mana Root:
     * - Compressed Origin Alloy x25 (center 5x5)
     * - Mana Wisdom x24 (concentric ring around center 5x5, i.e., 7x7 outer border)
     * - Star Mana x30 (outer 2 rows: top)
     * - Quantum Mana x30 (outer 2 columns: right)
     * - Abyssal Mana x30 (outer 2 rows: bottom)
     * - Marine Mana x30 (outer 2 columns: left)
     * Total = 25 + 24 + 30 + 30 + 30 + 30 = 169 slots.
     */
    private static ExtendedCraftingRecipe createManaRootRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack alloy = new ItemStack(ModItems.COMPRESSED_ORIGIN_ALLOY.get());
        ItemStack wisdom = new ItemStack(ModItems.MANA_WISDOM.get());
        ItemStack star = new ItemStack(ModItems.STAR_MANA.get());
        ItemStack quantum = new ItemStack(ModItems.QUANTUM_MANA.get());
        ItemStack abyssal = new ItemStack(ModItems.ABYSSAL_MANA.get());
        ItemStack marine = new ItemStack(ModItems.MARINE_MANA.get());

        // Center 5x5: start 4, end 9
        // 7x7 border: start 3, end 10
        // Outer 120 slots (rings 0..2)
        int starCount = 0;
        int quantumCount = 0;
        int abyssalCount = 0;
        int marineCount = 0;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (r >= 4 && r <= 8 && c >= 4 && c <= 8) {
                    inputs.add(Collections.singletonList(alloy.copy()));
                } else if (r >= 3 && r <= 9 && c >= 3 && c <= 9) {
                    inputs.add(Collections.singletonList(wisdom.copy()));
                } else {
                    // Outer 120 slots allocated evenly: Top 30, Right 30, Bottom 30, Left 30
                    if (r < 3 && starCount < 30) {
                        inputs.add(Collections.singletonList(star.copy()));
                        starCount++;
                    } else if (c >= 10 && quantumCount < 30) {
                        inputs.add(Collections.singletonList(quantum.copy()));
                        quantumCount++;
                    } else if (r >= 10 && abyssalCount < 30) {
                        inputs.add(Collections.singletonList(abyssal.copy()));
                        abyssalCount++;
                    } else {
                        inputs.add(Collections.singletonList(marine.copy()));
                        marineCount++;
                    }
                }
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Origin Sword: Vanilla sword pattern scaled on 13x13 grid.
     */
    private static ExtendedCraftingRecipe createOriginSwordRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack root = new ItemStack(ModItems.MANA_ROOT.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                // Blade: Column 6, rows 1..8; Guard: Row 9, cols 4..8; Handle: Col 6, rows 10..11
                boolean isBlade = (c == 6 && r >= 1 && r <= 8);
                boolean isGuard = (r == 9 && c >= 4 && c <= 8);
                boolean isHandle = (c == 6 && r >= 10 && r <= 11);
                boolean isPart = isBlade || isGuard || isHandle;
                inputs.add(isPart ? Collections.singletonList(root.copy()) : Collections.emptyList());
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Origin Helmet: Vanilla helmet shape scaled on 13x13.
     */
    private static ExtendedCraftingRecipe createOriginHelmetRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack root = new ItemStack(ModItems.MANA_ROOT.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                // Helmet arc: Rows 3..5 across cols 3..9, and ears down on rows 6..8 cols 3..4 and 8..9
                boolean top = (r >= 3 && r <= 4 && c >= 3 && c <= 9);
                boolean ears = (r >= 5 && r <= 8 && ((c >= 3 && c <= 4) || (c >= 8 && c <= 9)));
                boolean isPart = top || ears;
                inputs.add(isPart ? Collections.singletonList(root.copy()) : Collections.emptyList());
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Origin Chestplate: Vanilla chestplate shape scaled on 13x13.
     */
    private static ExtendedCraftingRecipe createOriginChestplateRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack root = new ItemStack(ModItems.MANA_ROOT.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                // Shoulders: row 2..3 cols 2..4 and 8..10
                // Body: rows 4..10 cols 2..10
                boolean shoulders = (r >= 2 && r <= 3 && ((c >= 2 && c <= 4) || (c >= 8 && c <= 10)));
                boolean body = (r >= 4 && r <= 10 && c >= 2 && c <= 10);
                boolean isPart = shoulders || body;
                inputs.add(isPart ? Collections.singletonList(root.copy()) : Collections.emptyList());
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Origin Leggings: Vanilla leggings shape scaled on 13x13.
     */
    private static ExtendedCraftingRecipe createOriginLeggingsRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack root = new ItemStack(ModItems.MANA_ROOT.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                // Waist: rows 2..4 cols 3..9
                // Legs: rows 5..10 cols 3..5 and 7..9
                boolean waist = (r >= 2 && r <= 4 && c >= 3 && c <= 9);
                boolean legs = (r >= 5 && r <= 10 && ((c >= 3 && c <= 5) || (c >= 7 && c <= 9)));
                boolean isPart = waist || legs;
                inputs.add(isPart ? Collections.singletonList(root.copy()) : Collections.emptyList());
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Origin Boots: Vanilla boots shape scaled on 13x13.
     */
    private static ExtendedCraftingRecipe createOriginBootsRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack root = new ItemStack(ModItems.MANA_ROOT.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                // Two boots: rows 5..9 cols 3..5 and 7..9
                boolean isPart = (r >= 5 && r <= 9 && ((c >= 3 && c <= 5) || (c >= 7 && c <= 9)));
                inputs.add(isPart ? Collections.singletonList(root.copy()) : Collections.emptyList());
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    /**
     * Infinite Mana:
     * - 4 corners: Origin Armor pieces (Helmet at top-left, Chestplate at top-right, Leggings at bottom-left, Boots at bottom-right)
     * - Center (6,6): Origin Sword
     * - Cross around center (row 6 col 5, row 6 col 7, row 5 col 6, row 7 col 6): Mana Root x4
     */
    private static ExtendedCraftingRecipe createInfiniteManaRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack root = new ItemStack(ModItems.MANA_ROOT.get());
        ItemStack sword = new ItemStack(ModItems.ORIGIN_SWORD.get());
        ItemStack helmet = new ItemStack(ModItems.ORIGIN_HELMET.get());
        ItemStack chest = new ItemStack(ModItems.ORIGIN_CHESTPLATE.get());
        ItemStack leggings = new ItemStack(ModItems.ORIGIN_LEGGINGS.get());
        ItemStack boots = new ItemStack(ModItems.ORIGIN_BOOTS.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (r == 0 && c == 0) inputs.add(Collections.singletonList(helmet.copy()));
                else if (r == 0 && c == 12) inputs.add(Collections.singletonList(chest.copy()));
                else if (r == 12 && c == 0) inputs.add(Collections.singletonList(leggings.copy()));
                else if (r == 12 && c == 12) inputs.add(Collections.singletonList(boots.copy()));
                else if (r == 6 && c == 6) inputs.add(Collections.singletonList(sword.copy()));
                else if ((r == 5 && c == 6) || (r == 7 && c == 6) || (r == 6 && c == 5) || (r == 6 && c == 7)) {
                    inputs.add(Collections.singletonList(root.copy()));
                } else {
                    inputs.add(Collections.emptyList());
                }
            }
        }
        return new ExtendedCraftingRecipe(10, gridSize, inputs, output, name);
    }

    private static ExtendedCraftingRecipe createManaKnowledgeRecipe() {
        int gridSize = 13;
        int totalSlots = gridSize * gridSize; // 169
        List<List<ItemStack>> inputs = new ArrayList<>(totalSlots);

        Item[] manas = new Item[]{
                ModItems.TIER0_MANA.get(), ModItems.TIER1_MANA.get(), ModItems.TIER2_MANA.get(),
                ModItems.TIER3_MANA.get(), ModItems.TIER4_MANA.get(), ModItems.TIER5_MANA.get(),
                ModItems.TIER6_MANA.get(), ModItems.TIER7_MANA.get(), ModItems.TIER8_MANA.get(),
                ModItems.TIER9_MANA.get(), ModItems.TIER10_MANA.get(), ModItems.PURE_MANA.get()
        };

        Item[] ores = new Item[]{
                ModBlocks.TIER0_MANA_ORE.get().asItem(), ModBlocks.TIER1_MANA_ORE.get().asItem(),
                ModBlocks.TIER2_MANA_ORE.get().asItem(), ModBlocks.TIER3_MANA_ORE.get().asItem(),
                ModBlocks.TIER4_MANA_ORE.get().asItem(), ModBlocks.TIER5_MANA_ORE.get().asItem(),
                ModBlocks.TIER6_MANA_ORE.get().asItem(), ModBlocks.TIER7_MANA_ORE.get().asItem(),
                ModBlocks.TIER8_MANA_ORE.get().asItem(), ModBlocks.TIER9_MANA_ORE.get().asItem(),
                ModBlocks.TIER10_MANA_ORE.get().asItem(), ModBlocks.PURE_MANA_ORE.get().asItem()
        };

        Item[] catalysts = new Item[]{
                ModBlocks.FAIRY_BLOCK.get().asItem(), // Tier 0 catalyst slot is Fairy
                ModItems.TIER1_FAIRY_CATALYST.get(), ModItems.TIER2_FAIRY_CATALYST.get(),
                ModItems.TIER3_FAIRY_CATALYST.get(), ModItems.TIER4_FAIRY_CATALYST.get(),
                ModItems.TIER5_FAIRY_CATALYST.get(), ModItems.TIER6_FAIRY_CATALYST.get(),
                ModItems.TIER7_FAIRY_CATALYST.get(), ModItems.TIER8_FAIRY_CATALYST.get(),
                ModItems.TIER9_FAIRY_CATALYST.get(), ModItems.TIER10_FAIRY_CATALYST.get(),
                ModItems.PURE_FAIRY_CATALYST.get()
        };

        // Combine all 36 items: 12 manas, 12 ores, 12 catalysts
        List<ItemStack> pool = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            pool.add(new ItemStack(manas[i]));
            pool.add(new ItemStack(ores[i]));
            pool.add(new ItemStack(catalysts[i]));
        }

        // Fill all 169 slots cyclically with the 36 items
        for (int i = 0; i < totalSlots; i++) {
            ItemStack item = pool.get(i % pool.size());
            inputs.add(Collections.singletonList(item.copy()));
        }

        return new ExtendedCraftingRecipe(
                new net.minecraft.resources.ResourceLocation(com.exponentialmana.mod.ExponentialMana.MOD_ID, "extended_crafting/tier10/mana_knowledge"),
                10, gridSize, inputs,
                new ItemStack(ModItems.MANA_KNOWLEDGE.get()),
                Component.translatable("item.exponential_mana.mana_knowledge"),
                true // Shapeless: 13x13 (169 items required) can be placed in any arrangement!
        );
    }
}
