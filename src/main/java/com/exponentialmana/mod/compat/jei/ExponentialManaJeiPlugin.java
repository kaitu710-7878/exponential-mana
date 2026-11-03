package com.exponentialmana.mod.compat.jei;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.registry.ModBlocks;
import com.exponentialmana.mod.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Optional JEI Integration Plugin for Exponential Mana.
 * Provides rich in-game recipe browsing, Tier 0 mana discovery info, comprehensive machine guides,
 * and comprehensive Extended Crafting Table recipes (Tier 1 4x4 up to Tier 10 13x13).
 * Gracefully dormant when JEI is not loaded in player's environment.
 */
@JeiPlugin
public class ExponentialManaJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation(ExponentialMana.MOD_ID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ExtendedCraftingJeiCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        // Register Extended Crafting Tables as catalysts
        for (var tableBlock : ModBlocks.EXTENDED_CRAFTING_TABLES) {
            ItemStack tableStack = new ItemStack(tableBlock.get());
            registration.addRecipeCatalyst(tableStack, RecipeTypes.CRAFTING);
            registration.addRecipeCatalyst(tableStack, ExtendedCraftingJeiCategory.TYPE);
        }
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FAIRY_BLOCK.get()), RecipeTypes.CRAFTING);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        // 1. Tier 0 Mana Acquisition Guides
        registration.addIngredientInfo(
                new ItemStack(ModItems.TIER0_MANA.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.tier0_mana.info")
        );

        registration.addIngredientInfo(
                new ItemStack(ModBlocks.INACTIVE_TIER0_MANA_ORE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.inactive_tier0_ore.info")
        );

        // 2. Machine & Automation System Guides
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.BATCH_ORE_BREAKER.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.batch_ore_breaker.info")
        );

        registration.addIngredientInfo(
                new ItemStack(ModBlocks.THAWER.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.thawer.info")
        );

        registration.addIngredientInfo(
                new ItemStack(ModBlocks.FAIRY_BLOCK.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.fairy_block.info")
        );

        List<ItemStack> compressedFairies = new ArrayList<>();
        for (var cf : ModBlocks.COMPRESSED_FAIRY_BLOCKS) {
            compressedFairies.add(new ItemStack(cf.get()));
        }
        registration.addIngredientInfo(
                compressedFairies,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.compressed_fairy.info")
        );

        registration.addIngredientInfo(
                new ItemStack(ModBlocks.FAIRY_ATTRACTOR.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.fairy_attractor.info")
        );

        // 3. Batch Compressed Mana & Infinite Mana Guides
        registration.addIngredientInfo(
                new ItemStack(ModItems.BATCH_COMPRESSED_MANA.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.batch_compressed_mana.info")
        );

        registration.addIngredientInfo(
                new ItemStack(ModItems.INFINITE_MANA.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.infinite_mana.info")
        );

        // 4. Extended Crafting Table Recipes in JEI (Tier 1 4x4 up to Tier 10 13x13)
        List<ExtendedCraftingJeiRecipe> extendedRecipes = createAllExtendedCraftingRecipes();
        registration.addRecipes(ExtendedCraftingJeiCategory.TYPE, extendedRecipes);
    }

    private List<ExtendedCraftingJeiRecipe> createAllExtendedCraftingRecipes() {
        List<ExtendedCraftingJeiRecipe> list = new ArrayList<>();

        // ==========================================
        // Tier 1 (4x4 Table)
        // ==========================================
        // Mana: Tier 0 Mana x8 + Coal (center)
        list.add(createCenterRecipe(1, 4,
                new ItemStack(ModItems.TIER0_MANA.get()),
                new ItemStack(Items.COAL),
                new ItemStack(ModItems.TIER1_MANA.get()),
                Component.translatable("item.exponential_mana.tier1_mana")));

        // Core: Tier 1 Mana x8 + Coal (center)
        list.add(createCenterRecipe(1, 4,
                new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(Items.COAL),
                new ItemStack(ModItems.TIER1_CORE.get()),
                Component.translatable("item.exponential_mana.tier1_core")));

        // Ore: Tier 1 Core x8 + Tier 0 Mana Ore (center)
        list.add(createCenterRecipe(1, 4,
                new ItemStack(ModItems.TIER1_CORE.get()),
                new ItemStack(ModBlocks.TIER0_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER1_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier1_mana_ore")));

        // Catalyst: Tier 1 Core x8 + Fairy Residue (center)
        list.add(createCenterRecipe(1, 4,
                new ItemStack(ModItems.TIER1_CORE.get()),
                new ItemStack(ModItems.FAIRY_RESIDUE.get()),
                new ItemStack(ModItems.TIER1_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier1_fairy_catalyst")));

        // Table: Tier 1 Core x8 + Crafting Table (center)
        list.add(createCenterRecipe(1, 4,
                new ItemStack(ModItems.TIER1_CORE.get()),
                new ItemStack(Items.CRAFTING_TABLE),
                new ItemStack(ModBlocks.TIER1_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier1_extended_crafting_table")));

        // ==========================================
        // Tier 2 (4x4 Table used) - Even-tier Shard Rule (4 shards + 12 cores/mana)
        // ==========================================
        // Mana: Tier 1 Mana x12 + Copper x4
        list.add(createBorderCenterRecipe(2, 4,
                new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(Items.COPPER_INGOT),
                new ItemStack(ModItems.TIER2_MANA.get()),
                Component.translatable("item.exponential_mana.tier2_mana")));

        // Core: Tier 2 Mana x12 + Copper x4
        list.add(createBorderCenterRecipe(2, 4,
                new ItemStack(ModItems.TIER2_MANA.get()),
                new ItemStack(Items.COPPER_INGOT),
                new ItemStack(ModItems.TIER2_CORE.get()),
                Component.translatable("item.exponential_mana.tier2_core")));

        // Ore: Tier 1 Mana Ore Shard x4 + Tier 2 Core x12
        list.add(createBorderCenterRecipe(2, 4,
                new ItemStack(ModItems.TIER2_CORE.get()),
                new ItemStack(ModItems.TIER1_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER2_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier2_mana_ore")));

        // Catalyst: Tier 1 Fairy Catalyst Shard x4 + Tier 2 Core x12
        list.add(createBorderCenterRecipe(2, 4,
                new ItemStack(ModItems.TIER2_CORE.get()),
                new ItemStack(ModItems.TIER1_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER2_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier2_fairy_catalyst")));

        // Table (5x5): Tier 1 Mini Crafting Table x4 + Tier 2 Core x12
        list.add(createBorderCenterRecipe(2, 4,
                new ItemStack(ModItems.TIER2_CORE.get()),
                new ItemStack(ModItems.TIER1_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER2_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier2_extended_crafting_table")));

        // ==========================================
        // Tier 3 (5x5 Table used) - Odd-tier Direct Upgrade Rule
        // ==========================================
        // Mana: Tier 2 Mana x24 + Redstone x1
        list.add(createSingleCenterRecipe(3, 5,
                new ItemStack(ModItems.TIER2_MANA.get()),
                new ItemStack(Items.REDSTONE),
                new ItemStack(ModItems.TIER3_MANA.get()),
                Component.translatable("item.exponential_mana.tier3_mana")));

        // Core: Tier 3 Mana x16 + Redstone x9 (3x3 center)
        list.add(createConcentricBoxRecipe(3, 5, 3,
                new ItemStack(ModItems.TIER3_MANA.get()),
                new ItemStack(Items.REDSTONE),
                new ItemStack(ModItems.TIER3_CORE.get()),
                Component.translatable("item.exponential_mana.tier3_core")));

        // Ore: Tier 3 Core x24 + Tier 2 Mana Ore x1
        list.add(createSingleCenterRecipe(3, 5,
                new ItemStack(ModItems.TIER3_CORE.get()),
                new ItemStack(ModBlocks.TIER2_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER3_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier3_mana_ore")));

        // Catalyst: Tier 3 Core x24 + Tier 2 Catalyst x1
        list.add(createSingleCenterRecipe(3, 5,
                new ItemStack(ModItems.TIER3_CORE.get()),
                new ItemStack(ModItems.TIER2_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER3_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier3_fairy_catalyst")));

        // Table (6x6): Tier 3 Core x24 + Tier 2 Table x1
        list.add(createSingleCenterRecipe(3, 5,
                new ItemStack(ModItems.TIER3_CORE.get()),
                new ItemStack(ModBlocks.TIER2_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER3_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier3_extended_crafting_table")));

        // ==========================================
        // Tier 4 (6x6 Table used) - Even-tier Shard Rule (4 shards + 32 cores)
        // ==========================================
        list.add(createBorderCenterRecipe(4, 6,
                new ItemStack(ModItems.TIER3_MANA.get()),
                new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(ModItems.TIER4_MANA.get()),
                Component.translatable("item.exponential_mana.tier4_mana")));

        list.add(createBorderCenterRecipe(4, 6,
                new ItemStack(ModItems.TIER4_MANA.get()),
                new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(ModItems.TIER4_CORE.get()),
                Component.translatable("item.exponential_mana.tier4_core")));

        list.add(createBorderCenterRecipe(4, 6,
                new ItemStack(ModItems.TIER4_CORE.get()),
                new ItemStack(ModItems.TIER3_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER4_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier4_mana_ore")));

        list.add(createBorderCenterRecipe(4, 6,
                new ItemStack(ModItems.TIER4_CORE.get()),
                new ItemStack(ModItems.TIER3_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER4_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier4_fairy_catalyst")));

        list.add(createBorderCenterRecipe(4, 6,
                new ItemStack(ModItems.TIER4_CORE.get()),
                new ItemStack(ModItems.TIER3_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER4_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier4_extended_crafting_table")));

        // ==========================================
        // Tier 5 (7x7 Table used) - Odd-tier Direct Upgrade Rule
        // ==========================================
        list.add(createSingleCenterRecipe(5, 7,
                new ItemStack(ModItems.TIER4_MANA.get()),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(ModItems.TIER5_MANA.get()),
                Component.translatable("item.exponential_mana.tier5_mana")));

        list.add(createConcentricBoxRecipe(5, 7, 3,
                new ItemStack(ModItems.TIER5_MANA.get()),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(ModItems.TIER5_CORE.get()),
                Component.translatable("item.exponential_mana.tier5_core")));

        list.add(createSingleCenterRecipe(5, 7,
                new ItemStack(ModItems.TIER5_CORE.get()),
                new ItemStack(ModBlocks.TIER4_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER5_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier5_mana_ore")));

        list.add(createSingleCenterRecipe(5, 7,
                new ItemStack(ModItems.TIER5_CORE.get()),
                new ItemStack(ModItems.TIER4_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER5_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier5_fairy_catalyst")));

        list.add(createSingleCenterRecipe(5, 7,
                new ItemStack(ModItems.TIER5_CORE.get()),
                new ItemStack(ModBlocks.TIER4_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER5_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier5_extended_crafting_table")));

        // ==========================================
        // Tier 6 (8x8 Table used) - Even-tier Shard Rule (4 shards + 60 cores)
        // ==========================================
        list.add(createBorderCenterRecipe(6, 8,
                new ItemStack(ModItems.TIER5_MANA.get()),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ModItems.TIER6_MANA.get()),
                Component.translatable("item.exponential_mana.tier6_mana")));

        list.add(createConcentricBoxRecipe(6, 8, 4,
                new ItemStack(ModItems.TIER6_MANA.get()),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ModItems.TIER6_CORE.get()),
                Component.translatable("item.exponential_mana.tier6_core")));

        list.add(createBorderCenterRecipe(6, 8,
                new ItemStack(ModItems.TIER6_CORE.get()),
                new ItemStack(ModItems.TIER5_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER6_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier6_mana_ore")));

        list.add(createBorderCenterRecipe(6, 8,
                new ItemStack(ModItems.TIER6_CORE.get()),
                new ItemStack(ModItems.TIER5_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER6_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier6_fairy_catalyst")));

        list.add(createBorderCenterRecipe(6, 8,
                new ItemStack(ModItems.TIER6_CORE.get()),
                new ItemStack(ModItems.TIER5_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER6_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier6_extended_crafting_table")));

        // ==========================================
        // Tier 7 (9x9 Table used) - Odd-tier Direct Upgrade Rule
        // ==========================================
        list.add(createSingleCenterRecipe(7, 9,
                new ItemStack(ModItems.TIER6_MANA.get()),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ModItems.TIER7_MANA.get()),
                Component.translatable("item.exponential_mana.tier7_mana")));

        list.add(createConcentricBoxRecipe(7, 9, 3,
                new ItemStack(ModItems.TIER7_MANA.get()),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ModItems.TIER7_CORE.get()),
                Component.translatable("item.exponential_mana.tier7_core")));

        list.add(createSingleCenterRecipe(7, 9,
                new ItemStack(ModItems.TIER7_CORE.get()),
                new ItemStack(ModBlocks.TIER6_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER7_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier7_mana_ore")));

        list.add(createSingleCenterRecipe(7, 9,
                new ItemStack(ModItems.TIER7_CORE.get()),
                new ItemStack(ModItems.TIER6_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER7_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier7_fairy_catalyst")));

        list.add(createSingleCenterRecipe(7, 9,
                new ItemStack(ModItems.TIER7_CORE.get()),
                new ItemStack(ModBlocks.TIER6_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER7_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier7_extended_crafting_table")));

        // ==========================================
        // Tier 8 (10x10 Table used) - Even-tier Shard Rule (4 shards + 96 cores)
        // ==========================================
        list.add(createBorderCenterRecipe(8, 10,
                new ItemStack(ModItems.TIER7_MANA.get()),
                new ItemStack(Items.OBSIDIAN),
                new ItemStack(ModItems.TIER8_MANA.get()),
                Component.translatable("item.exponential_mana.tier8_mana")));

        list.add(createConcentricBoxRecipe(8, 10, 6,
                new ItemStack(ModItems.TIER8_MANA.get()),
                new ItemStack(Items.OBSIDIAN),
                new ItemStack(ModItems.TIER8_CORE.get()),
                Component.translatable("item.exponential_mana.tier8_core")));

        list.add(createBorderCenterRecipe(8, 10,
                new ItemStack(ModItems.TIER8_CORE.get()),
                new ItemStack(ModItems.TIER7_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER8_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier8_mana_ore")));

        list.add(createBorderCenterRecipe(8, 10,
                new ItemStack(ModItems.TIER8_CORE.get()),
                new ItemStack(ModItems.TIER7_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER8_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier8_fairy_catalyst")));

        list.add(createBorderCenterRecipe(8, 10,
                new ItemStack(ModItems.TIER8_CORE.get()),
                new ItemStack(ModItems.TIER7_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER8_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier8_extended_crafting_table")));

        // ==========================================
        // Tier 9 (11x11 Table used) - Odd-tier Direct Upgrade Rule
        // ==========================================
        list.add(createConcentricBoxRecipe(9, 11, 3,
                new ItemStack(ModItems.TIER8_MANA.get()),
                new ItemStack(Items.EMERALD),
                new ItemStack(ModItems.TIER9_MANA.get()),
                Component.translatable("item.exponential_mana.tier9_mana")));

        list.add(createConcentricBoxRecipe(9, 11, 3,
                new ItemStack(ModItems.TIER9_MANA.get()),
                new ItemStack(Items.EMERALD),
                new ItemStack(ModItems.TIER9_CORE.get()),
                Component.translatable("item.exponential_mana.tier9_core")));

        list.add(createSingleCenterRecipe(9, 11,
                new ItemStack(ModItems.TIER9_CORE.get()),
                new ItemStack(ModBlocks.TIER8_MANA_ORE.get()),
                new ItemStack(ModBlocks.TIER9_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier9_mana_ore")));

        list.add(createSingleCenterRecipe(9, 11,
                new ItemStack(ModItems.TIER9_CORE.get()),
                new ItemStack(ModItems.TIER8_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.TIER9_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier9_fairy_catalyst")));

        list.add(createSingleCenterRecipe(9, 11,
                new ItemStack(ModItems.TIER9_CORE.get()),
                new ItemStack(ModBlocks.TIER8_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER9_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier9_extended_crafting_table")));

        // ==========================================
        // Tier 10 (12x12 Table used) - Even-tier Shard Rule (4 shards + 140 cores)
        // ==========================================
        list.add(createConcentricBoxRecipe(10, 12, 4,
                new ItemStack(ModItems.TIER9_MANA.get()),
                new ItemStack(Items.NETHERITE_INGOT),
                new ItemStack(ModItems.TIER10_MANA.get()),
                Component.translatable("item.exponential_mana.tier10_mana")));

        list.add(createConcentricBoxRecipe(10, 12, 4,
                new ItemStack(ModItems.TIER10_MANA.get()),
                new ItemStack(Items.NETHERITE_INGOT),
                new ItemStack(ModItems.TIER10_CORE.get()),
                Component.translatable("item.exponential_mana.tier10_core")));

        list.add(createBorderCenterRecipe(10, 12,
                new ItemStack(ModItems.TIER10_CORE.get()),
                new ItemStack(ModItems.TIER9_MANA_ORE_SHARD.get()),
                new ItemStack(ModBlocks.TIER10_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.tier10_mana_ore")));

        list.add(createBorderCenterRecipe(10, 12,
                new ItemStack(ModItems.TIER10_CORE.get()),
                new ItemStack(ModItems.TIER9_FAIRY_CATALYST_SHARD.get()),
                new ItemStack(ModItems.TIER10_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.tier10_fairy_catalyst")));

        list.add(createBorderCenterRecipe(10, 12,
                new ItemStack(ModItems.TIER10_CORE.get()),
                new ItemStack(ModItems.TIER9_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER10_EXTENDED_CRAFTING_TABLE.get()),
                Component.translatable("block.exponential_mana.tier10_extended_crafting_table")));

        // ==========================================
        // End-Game (13x13 Table used - 169 Slots)
        // ==========================================
        // Pure Mana: 11 compressed manas x 15 each
        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.PURE_MANA.get()),
                new ItemStack(ModItems.PURE_MANA.get()),
                Component.translatable("item.exponential_mana.pure_mana")));

        // Pure Mana Ore: Pure Mana x168 + Tier 10 Ore x1
        list.add(createSingleCenterRecipe(10, 13,
                new ItemStack(ModItems.PURE_MANA.get()),
                new ItemStack(ModBlocks.TIER10_MANA_ORE.get()),
                new ItemStack(ModBlocks.PURE_MANA_ORE.get()),
                Component.translatable("block.exponential_mana.pure_mana_ore")));

        // Pure Fairy Catalyst: Pure Mana x168 + Tier 10 Catalyst x1
        list.add(createSingleCenterRecipe(10, 13,
                new ItemStack(ModItems.PURE_MANA.get()),
                new ItemStack(ModItems.TIER10_FAIRY_CATALYST.get()),
                new ItemStack(ModItems.PURE_FAIRY_CATALYST.get()),
                Component.translatable("item.exponential_mana.pure_fairy_catalyst")));

        // Origin Alloy
        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.ORIGIN_ALLOY.get()),
                new ItemStack(ModItems.ORIGIN_ALLOY.get()),
                Component.translatable("item.exponential_mana.origin_alloy")));

        // Compressed Origin Alloy
        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.ORIGIN_ALLOY.get()),
                new ItemStack(ModItems.COMPRESSED_ORIGIN_ALLOY.get()),
                Component.translatable("item.exponential_mana.compressed_origin_alloy")));

        // Mana Wisdom: Mana Knowledge x169
        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.MANA_KNOWLEDGE.get()),
                new ItemStack(ModItems.MANA_WISDOM.get()),
                Component.translatable("item.exponential_mana.mana_wisdom")));

        // 4 Root Manas
        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_STAR_MANA.get()),
                new ItemStack(ModItems.STAR_MANA.get()),
                Component.translatable("item.exponential_mana.star_mana")));

        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_QUANTUM_MANA.get()),
                new ItemStack(ModItems.QUANTUM_MANA.get()),
                Component.translatable("item.exponential_mana.quantum_mana")));

        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_ABYSSAL_MANA.get()),
                new ItemStack(ModItems.ABYSSAL_MANA.get()),
                Component.translatable("item.exponential_mana.abyssal_mana")));

        list.add(createFullGridRecipe(10, 13,
                new ItemStack(ModItems.UNCOMPRESSED_MARINE_MANA.get()),
                new ItemStack(ModItems.MARINE_MANA.get()),
                Component.translatable("item.exponential_mana.marine_mana")));

        // Mana Root
        list.add(createRootRecipe(
                new ItemStack(ModItems.MANA_ROOT.get()),
                Component.translatable("item.exponential_mana.mana_root")));

        // Origin Equipment
        list.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_SWORD.get()),
                Component.translatable("item.exponential_mana.origin_sword")));

        list.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_HELMET.get()),
                Component.translatable("item.exponential_mana.origin_helmet")));

        list.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_CHESTPLATE.get()),
                Component.translatable("item.exponential_mana.origin_chestplate")));

        list.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_LEGGINGS.get()),
                Component.translatable("item.exponential_mana.origin_leggings")));

        list.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_BOOTS.get()),
                Component.translatable("item.exponential_mana.origin_boots")));

        // Infinite Mana
        list.add(createInfiniteManaRecipe(
                new ItemStack(ModItems.INFINITE_MANA.get()),
                Component.translatable("item.exponential_mana.infinite_mana")));

        return list;
    }

    private ExtendedCraftingJeiRecipe createCenterRecipe(int tier, int gridSize, ItemStack outer, ItemStack center, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean isCenter = (r >= 1 && r <= 2 && c >= 1 && c <= 2);
                inputs.add(Collections.singletonList(isCenter ? center.copy() : outer.copy()));
            }
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, name);
    }

    private ExtendedCraftingJeiRecipe createBorderCenterRecipe(int tier, int gridSize, ItemStack outer, ItemStack center, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        int midStart = gridSize / 2 - 1;
        int midEnd = gridSize / 2;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean isCenter = (r >= midStart && r <= midEnd && c >= midStart && c <= midEnd);
                inputs.add(Collections.singletonList(isCenter ? center.copy() : outer.copy()));
            }
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, name);
    }

    private ExtendedCraftingJeiRecipe createSingleCenterRecipe(int tier, int gridSize, ItemStack outer, ItemStack center, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        int mid = gridSize / 2;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean isCenter = (r == mid && c == mid);
                inputs.add(Collections.singletonList(isCenter ? center.copy() : outer.copy()));
            }
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, name);
    }

    private ExtendedCraftingJeiRecipe createConcentricBoxRecipe(int tier, int gridSize, int boxSize, ItemStack outer, ItemStack inner, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        int start = (gridSize - boxSize) / 2;
        int end = start + boxSize;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean isInner = (r >= start && r < end && c >= start && c < end);
                inputs.add(Collections.singletonList(isInner ? inner.copy() : outer.copy()));
            }
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, name);
    }

    private ExtendedCraftingJeiRecipe createFullGridRecipe(int tier, int gridSize, ItemStack fill, ItemStack output, Component name) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        for (int i = 0; i < gridSize * gridSize; i++) {
            inputs.add(Collections.singletonList(fill.copy()));
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, name);
    }

    private ExtendedCraftingJeiRecipe createRootRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack alloy = new ItemStack(ModItems.COMPRESSED_ORIGIN_ALLOY.get());
        ItemStack wisdom = new ItemStack(ModItems.MANA_WISDOM.get());
        ItemStack star = new ItemStack(ModItems.STAR_MANA.get());
        ItemStack quantum = new ItemStack(ModItems.QUANTUM_MANA.get());
        ItemStack abyssal = new ItemStack(ModItems.ABYSSAL_MANA.get());
        ItemStack marine = new ItemStack(ModItems.MARINE_MANA.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (r < 3 && c < 3) inputs.add(Collections.singletonList(alloy.copy()));
                else if (r >= 10 && c >= 10) inputs.add(Collections.singletonList(wisdom.copy()));
                else if (r < 6) inputs.add(Collections.singletonList(star.copy()));
                else if (c < 6) inputs.add(Collections.singletonList(quantum.copy()));
                else if (r > 6) inputs.add(Collections.singletonList(abyssal.copy()));
                else inputs.add(Collections.singletonList(marine.copy()));
            }
        }
        return new ExtendedCraftingJeiRecipe(10, 13, inputs, output, name);
    }

    private ExtendedCraftingJeiRecipe createOriginItemRecipe(ItemStack output, Component name) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack root = new ItemStack(ModItems.MANA_ROOT.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                // Classic shape template expanded to 13x13
                boolean shape = (r >= 2 && r <= 10 && c >= 2 && c <= 10);
                inputs.add(Collections.singletonList(shape ? root.copy() : ItemStack.EMPTY));
            }
        }
        return new ExtendedCraftingJeiRecipe(10, 13, inputs, output, name);
    }

    private ExtendedCraftingJeiRecipe createInfiniteManaRecipe(ItemStack output, Component name) {
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
                else if (r >= 5 && r <= 7 && c >= 5 && c <= 7) inputs.add(Collections.singletonList(root.copy()));
                else inputs.add(Collections.emptyList());
            }
        }
        return new ExtendedCraftingJeiRecipe(10, 13, inputs, output, name);
    }
}
