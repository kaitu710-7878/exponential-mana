package com.exponentialmana.mod.recipe;

import com.exponentialmana.mod.ExponentialMana;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Common recipe definition for Extended Crafting Tables (Tier 1 4x4 to Tier 10 13x13).
 * Usable both by JEI display (including bookmark/favorites support) and in-game crafting engine (manual crafting + 1t/craft automation).
 */
public class ExtendedCraftingRecipe {
    private final ResourceLocation id;
    private final int tier;
    private final int gridSize;
    private final List<List<ItemStack>> inputs; // gridSize * gridSize list of valid items per slot
    private final ItemStack output;
    private final Component recipeName;

    public ExtendedCraftingRecipe(ResourceLocation id, int tier, int gridSize, List<List<ItemStack>> inputs, ItemStack output, Component recipeName) {
        this.id = id != null ? id : generateDefaultId(tier, output);
        this.tier = tier;
        this.gridSize = gridSize;
        this.inputs = inputs;
        this.output = output;
        this.recipeName = recipeName;
    }

    public ExtendedCraftingRecipe(int tier, int gridSize, List<List<ItemStack>> inputs, ItemStack output, Component recipeName) {
        this(null, tier, gridSize, inputs, output, recipeName);
    }

    private static ResourceLocation generateDefaultId(int tier, ItemStack output) {
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(output.getItem());
        String path = itemKey != null ? itemKey.getPath() : "unknown_recipe";
        return new ResourceLocation(ExponentialMana.MOD_ID, "extended_crafting/tier" + tier + "/" + path);
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getTier() {
        return tier;
    }

    public int getGridSize() {
        return gridSize;
    }

    public List<List<ItemStack>> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public Component getRecipeName() {
        return recipeName != null ? recipeName : output.getHoverName();
    }

    /**
     * Checks if the items in the crafting grid match this recipe.
     * @param grid The item handler for the crafting grid (slot 0 to gridSize*gridSize - 1)
     */
    public boolean matches(IItemHandler grid) {
        int totalSlots = gridSize * gridSize;
        if (grid.getSlots() < totalSlots || inputs.size() < totalSlots) {
            return false;
        }

        for (int i = 0; i < totalSlots; i++) {
            ItemStack inGrid = grid.getStackInSlot(i);
            List<ItemStack> allowed = inputs.get(i);

            if (allowed == null || allowed.isEmpty()) {
                if (!inGrid.isEmpty()) return false;
            } else {
                boolean matchSlot = false;
                for (ItemStack candidate : allowed) {
                    if (candidate.isEmpty()) {
                        if (inGrid.isEmpty()) {
                            matchSlot = true;
                            break;
                        }
                    } else if (!inGrid.isEmpty() && inGrid.is(candidate.getItem())) {
                        if (candidate.hasTag()) {
                            if (ItemStack.isSameItemSameTags(inGrid, candidate)) {
                                matchSlot = true;
                                break;
                            }
                        } else {
                            matchSlot = true;
                            break;
                        }
                    }
                }
                if (!matchSlot) {
                    return false;
                }
            }
        }
        return true;
    }
}
