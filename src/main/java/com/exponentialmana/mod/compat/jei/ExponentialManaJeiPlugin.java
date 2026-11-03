package com.exponentialmana.mod.compat.jei;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.block.entity.ExtendedCraftingTableBlockEntity;
import com.exponentialmana.mod.menu.ExtendedCraftingMenu;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipe;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipeManager;
import com.exponentialmana.mod.registry.ModBlocks;
import com.exponentialmana.mod.registry.ModItems;
import com.exponentialmana.mod.registry.ModMenuTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Optional JEI Integration Plugin for Exponential Mana.
 * Features:
 * - 10 Dedicated Extended Crafting Categories (Tier 1 4x4 up to Tier 10 13x13), matching Extended Crafting Reforked.
 * - Standard 18x18 slot pitch with clean backgrounds preventing overlapping.
 * - Full Recipe Transfer Handler support ("+" button to auto-place ingredients from player inventory into tables).
 * - Transfer button moved below output slot to prevent overlapping crafting grid.
 * - Full guides and catalyst registrations.
 */
@JeiPlugin
public class ExponentialManaJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation(ExponentialMana.MOD_ID, "jei_plugin");

    private final Map<Integer, ExtendedCraftingJeiCategory> categories = new HashMap<>();

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        categories.clear();
        for (int tier = 1; tier <= 10; tier++) {
            ExtendedCraftingJeiCategory cat = new ExtendedCraftingJeiCategory(tier, registration.getJeiHelpers().getGuiHelper());
            categories.put(tier, cat);
            registration.addRecipeCategories(cat);
        }
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        for (int tier = 1; tier <= 10; tier++) {
            ItemStack tableStack = new ItemStack(ModBlocks.EXTENDED_CRAFTING_TABLES.get(tier - 1).get());
            registration.addRecipeCatalyst(tableStack, RecipeTypes.CRAFTING);
            ExtendedCraftingJeiCategory cat = categories.get(tier);
            if (cat != null) {
                registration.addRecipeCatalyst(tableStack, cat.getRecipeType());
            }
        }
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FAIRY_BLOCK.get()), RecipeTypes.CRAFTING);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        // Register "+" transfer button for each Extended Crafting Table tier
        for (int tier = 1; tier <= 10; tier++) {
            ExtendedCraftingJeiCategory cat = categories.get(tier);
            if (cat != null) {
                int gridSize = cat.getGridSize();
                int inputCount = gridSize * gridSize;
                // recipeSlotStart = 0, recipeSlotCount = inputCount
                // inventorySlotStart = inputCount (grid) + 1 (manual result) + 18 (buffer) + 9 (auto output)
                // inventorySlotCount = 36 (player inventory 27 + hotbar 9)
                int invStart = inputCount + 1 + ExtendedCraftingTableBlockEntity.BUFFER_SLOTS + ExtendedCraftingTableBlockEntity.AUTO_OUTPUT_SLOTS;
                registration.addRecipeTransferHandler(
                        ExtendedCraftingMenu.class,
                        ModMenuTypes.EXTENDED_CRAFTING_MENU.get(),
                        cat.getRecipeType(),
                        0,
                        inputCount,
                        invStart,
                        36
                );
            }
        }
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

        // 4. Register Extended Crafting recipes from ExtendedCraftingRecipeManager
        for (int tier = 1; tier <= 10; tier++) {
            ExtendedCraftingJeiCategory cat = categories.get(tier);
            List<ExtendedCraftingRecipe> recipes = ExtendedCraftingRecipeManager.getRecipesForTier(tier);
            if (cat != null && recipes != null && !recipes.isEmpty()) {
                registration.addRecipes(cat.getRecipeType(), recipes);
            }
        }
    }
}
