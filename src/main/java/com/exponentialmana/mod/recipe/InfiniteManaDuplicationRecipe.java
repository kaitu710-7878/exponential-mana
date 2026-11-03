package com.exponentialmana.mod.recipe;

import com.exponentialmana.mod.registry.ModItems;
import com.exponentialmana.mod.registry.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Custom crafting recipe for Infinite Mana item duplication.
 * Duplicates 1 target item into a full stack (up to 64).
 * Strictly forbids duplicating items with NBT (enchanted tools, filled shulkers, etc.) or Infinite Mana itself.
 */
public class InfiniteManaDuplicationRecipe extends CustomRecipe {
    public InfiniteManaDuplicationRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasInfiniteMana = false;
        ItemStack targetStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.INFINITE_MANA.get())) {
                if (hasInfiniteMana) return false; // More than 1 infinite mana
                hasInfiniteMana = true;
            } else {
                if (!targetStack.isEmpty()) return false; // More than 1 target item
                targetStack = stack;
            }
        }

        if (!hasInfiniteMana || targetStack.isEmpty()) {
            return false;
        }

        // Anti-exploit: Deny if target item has any NBT data
        if (targetStack.hasTag() && !targetStack.getTag().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack targetStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && !stack.is(ModItems.INFINITE_MANA.get())) {
                targetStack = stack;
                break;
            }
        }

        if (targetStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = targetStack.copy();
        result.setCount(Math.min(64, result.getMaxStackSize()));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.INFINITE_MANA_DUPLICATION.get();
    }
}
