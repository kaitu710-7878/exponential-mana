package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ExponentialMana.MOD_ID);

    public static final RegistryObject<RecipeType<ExtendedCraftingRecipe>> EXTENDED_CRAFTING =
            RECIPE_TYPES.register("extended_crafting", () -> RecipeType.simple(new net.minecraft.resources.ResourceLocation(ExponentialMana.MOD_ID, "extended_crafting")));

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
