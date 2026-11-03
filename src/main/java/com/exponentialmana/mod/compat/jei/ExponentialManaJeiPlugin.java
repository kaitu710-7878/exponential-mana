package com.exponentialmana.mod.compat.jei;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.registry.ModBlocks;
import com.exponentialmana.mod.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Optional JEI Integration Plugin for Exponential Mana.
 * Provides rich in-game recipe browsing, Tier 0 mana discovery info, and comprehensive machine guides.
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
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        // Register Extended Crafting Tables as crafting catalysts
        for (var tableBlock : ModBlocks.EXTENDED_CRAFTING_TABLES) {
            registration.addRecipeCatalyst(new ItemStack(tableBlock.get()), RecipeTypes.CRAFTING);
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

        // Compressed Fairies (Tier 1 to 10)
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

        // 4. Extended Crafting Tables Guide
        List<ItemStack> tables = new ArrayList<>();
        for (var t : ModBlocks.EXTENDED_CRAFTING_TABLES) {
            tables.add(new ItemStack(t.get()));
        }
        registration.addIngredientInfo(
                tables,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.extended_crafting_table.info")
        );

        // 5. Origin Equipment Guides
        registration.addIngredientInfo(
                List.of(
                        new ItemStack(ModItems.ORIGIN_HELMET.get()),
                        new ItemStack(ModItems.ORIGIN_CHESTPLATE.get()),
                        new ItemStack(ModItems.ORIGIN_LEGGINGS.get()),
                        new ItemStack(ModItems.ORIGIN_BOOTS.get()),
                        new ItemStack(ModItems.ORIGIN_SWORD.get())
                ),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.exponential_mana.origin_equipment.info")
        );
    }
}
