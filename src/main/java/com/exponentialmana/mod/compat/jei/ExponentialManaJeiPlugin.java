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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Optional JEI Integration Plugin for Exponential Mana.
 * Provides rich in-game recipe browsing, Tier 0 mana discovery info, comprehensive machine guides,
 * and Extended Crafting Table recipe visualization across all tiers (4x4 up to 13x13).
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
        // Register Extended Crafting Tables as catalysts for both vanilla crafting and Extended Crafting category
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

        // 4. Extended Crafting Table Recipes in JEI
        List<ExtendedCraftingJeiRecipe> extendedRecipes = createExtendedCraftingRecipes();
        registration.addRecipes(ExtendedCraftingJeiCategory.TYPE, extendedRecipes);
    }

    private List<ExtendedCraftingJeiRecipe> createExtendedCraftingRecipes() {
        List<ExtendedCraftingJeiRecipe> recipes = new ArrayList<>();

        // Tier 1 (4x4): Tier 1 Extended Crafting Table
        // Border: Tier 1 Mana (12), Center 2x2: Tier 1 Mini Crafting Table (4)
        recipes.add(createSurroundRecipe(1, 4,
                new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(ModItems.TIER1_MINI_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER1_EXTENDED_CRAFTING_TABLE.get()),
                "jei.exponential_mana.recipe.tier1_table"));

        // Tier 1 (4x4): Tier 1 Core
        // Border: Tier 1 Mana (12), Center 2x2: Tier 0 Mana (4)
        recipes.add(createSurroundRecipe(1, 4,
                new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(ModItems.TIER0_MANA.get()),
                new ItemStack(ModItems.TIER1_CORE.get()),
                "jei.exponential_mana.recipe.tier1_core"));

        // Tier 1 (4x4): Tier 1 Fairy Catalyst
        // Border: Tier 1 Mana (12), Center 2x2: Tier 1 Core (4)
        recipes.add(createSurroundRecipe(1, 4,
                new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(ModItems.TIER1_CORE.get()),
                new ItemStack(ModItems.TIER1_FAIRY_CATALYST.get()),
                "jei.exponential_mana.recipe.tier1_catalyst"));

        // Tier 1 (4x4): Compressed Fairy Block 1
        recipes.add(createSurroundRecipe(1, 4,
                new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(ModBlocks.FAIRY_BLOCK.get()),
                new ItemStack(ModBlocks.COMPRESSED_FAIRY_BLOCK_1.get()),
                "jei.exponential_mana.recipe.compressed_fairy_1"));

        // Tier 2 (5x5): Tier 2 Extended Crafting Table
        // Outer border: Tier 2 Mana, Inner: Tier 1 Mana, Center: Tier 1 Table
        recipes.add(createConcentricRecipe(2, 5,
                new ItemStack(ModItems.TIER2_MANA.get()),
                new ItemStack(ModItems.TIER1_MANA.get()),
                new ItemStack(ModBlocks.TIER1_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER2_EXTENDED_CRAFTING_TABLE.get()),
                "jei.exponential_mana.recipe.tier2_table"));

        // Tier 3 (6x6) to Tier 9 (12x12) Progression Recipes
        for (int tier = 3; tier <= 9; tier++) {
            int gridSize = tier + 3;
            Item outerMana = getManaItem(tier);
            Item innerMana = getManaItem(tier - 1);
            Block prevTable = ModBlocks.EXTENDED_CRAFTING_TABLES.get(tier - 2).get();
            Block currTable = ModBlocks.EXTENDED_CRAFTING_TABLES.get(tier - 1).get();

            recipes.add(createProgressiveRecipe(tier, gridSize,
                    new ItemStack(outerMana),
                    new ItemStack(innerMana),
                    new ItemStack(prevTable),
                    new ItemStack(currTable),
                    "jei.exponential_mana.recipe.tier" + tier + "_table"));
        }

        // Tier 10 (13x13): Tier 10 Extended Crafting Table
        recipes.add(createProgressiveRecipe(10, 13,
                new ItemStack(ModItems.TIER10_MANA.get()),
                new ItemStack(ModItems.TIER9_MANA.get()),
                new ItemStack(ModBlocks.TIER9_EXTENDED_CRAFTING_TABLE.get()),
                new ItemStack(ModBlocks.TIER10_EXTENDED_CRAFTING_TABLE.get()),
                "jei.exponential_mana.recipe.tier10_table"));

        // Tier 10 (13x13): Origin God Sword
        recipes.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_SWORD.get()),
                "jei.exponential_mana.recipe.origin_sword"));

        // Tier 10 (13x13): Origin Helmet
        recipes.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_HELMET.get()),
                "jei.exponential_mana.recipe.origin_helmet"));

        // Tier 10 (13x13): Origin Chestplate
        recipes.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_CHESTPLATE.get()),
                "jei.exponential_mana.recipe.origin_chestplate"));

        // Tier 10 (13x13): Origin Leggings
        recipes.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_LEGGINGS.get()),
                "jei.exponential_mana.recipe.origin_leggings"));

        // Tier 10 (13x13): Origin Boots
        recipes.add(createOriginItemRecipe(
                new ItemStack(ModItems.ORIGIN_BOOTS.get()),
                "jei.exponential_mana.recipe.origin_boots"));

        // Tier 10 (13x13): Infinite Mana
        recipes.add(createOriginItemRecipe(
                new ItemStack(ModItems.INFINITE_MANA.get()),
                "jei.exponential_mana.recipe.infinite_mana"));

        return recipes;
    }

    private ExtendedCraftingJeiRecipe createSurroundRecipe(int tier, int gridSize, ItemStack border, ItemStack center, ItemStack output, String descKey) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean isCenter = (r >= 1 && r < gridSize - 1 && c >= 1 && c < gridSize - 1);
                inputs.add(Collections.singletonList(isCenter ? center.copy() : border.copy()));
            }
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, descKey);
    }

    private ExtendedCraftingJeiRecipe createConcentricRecipe(int tier, int gridSize, ItemStack outer, ItemStack inner, ItemStack center, ItemStack output, String descKey) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        int mid = gridSize / 2;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (r == mid && c == mid) {
                    inputs.add(Collections.singletonList(center.copy()));
                } else if (r == 0 || r == gridSize - 1 || c == 0 || c == gridSize - 1) {
                    inputs.add(Collections.singletonList(outer.copy()));
                } else {
                    inputs.add(Collections.singletonList(inner.copy()));
                }
            }
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, descKey);
    }

    private ExtendedCraftingJeiRecipe createProgressiveRecipe(int tier, int gridSize, ItemStack outer, ItemStack inner, ItemStack center, ItemStack output, String descKey) {
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        int centerStart = (gridSize % 2 == 0) ? (gridSize / 2 - 1) : (gridSize / 2);
        int centerEnd = gridSize / 2;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (r >= centerStart && r <= centerEnd && c >= centerStart && c <= centerEnd) {
                    inputs.add(Collections.singletonList(center.copy()));
                } else if (r == 0 || r == gridSize - 1 || c == 0 || c == gridSize - 1) {
                    inputs.add(Collections.singletonList(outer.copy()));
                } else {
                    inputs.add(Collections.singletonList(inner.copy()));
                }
            }
        }
        return new ExtendedCraftingJeiRecipe(tier, gridSize, inputs, output, descKey);
    }

    private ExtendedCraftingJeiRecipe createOriginItemRecipe(ItemStack output, String descKey) {
        int gridSize = 13;
        List<List<ItemStack>> inputs = new ArrayList<>(gridSize * gridSize);
        ItemStack tier10Mana = new ItemStack(ModItems.TIER10_MANA.get());
        ItemStack originMana = new ItemStack(ModItems.MANA_ROOT.get());

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (r >= 5 && r <= 7 && c >= 5 && c <= 7) {
                    inputs.add(Collections.singletonList(originMana.copy()));
                } else {
                    inputs.add(Collections.singletonList(tier10Mana.copy()));
                }
            }
        }
        return new ExtendedCraftingJeiRecipe(10, 13, inputs, output, descKey);
    }

    private Item getManaItem(int tier) {
        return switch (tier) {
            case 1 -> ModItems.TIER1_MANA.get();
            case 2 -> ModItems.TIER2_MANA.get();
            case 3 -> ModItems.TIER3_MANA.get();
            case 4 -> ModItems.TIER4_MANA.get();
            case 5 -> ModItems.TIER5_MANA.get();
            case 6 -> ModItems.TIER6_MANA.get();
            case 7 -> ModItems.TIER7_MANA.get();
            case 8 -> ModItems.TIER8_MANA.get();
            case 9 -> ModItems.TIER9_MANA.get();
            case 10 -> ModItems.TIER10_MANA.get();
            default -> ModItems.TIER0_MANA.get();
        };
    }
}
