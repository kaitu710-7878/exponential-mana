package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.recipe.InfiniteManaDuplicationRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExponentialMana.MOD_ID);

    public static final RegistryObject<RecipeSerializer<InfiniteManaDuplicationRecipe>> INFINITE_MANA_DUPLICATION =
            RECIPE_SERIALIZERS.register("crafting_special_infinite_mana_duplication",
                    () -> new SimpleCraftingRecipeSerializer<>(InfiniteManaDuplicationRecipe::new));

    public static final RegistryObject<RecipeSerializer<com.exponentialmana.mod.recipe.ExtendedCraftingRecipe>> EXTENDED_CRAFTING =
            RECIPE_SERIALIZERS.register("extended_crafting",
                    () -> new com.exponentialmana.mod.recipe.ExtendedCraftingRecipe.Serializer());

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
