package com.exponentialmana.mod.recipe;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.registry.ModRecipeSerializers;
import com.exponentialmana.mod.registry.ModRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Universal JSON-driven Recipe definition for Extended Crafting Tables (Tier 1 4x4 up to Tier 10 13x13).
 * Supports data-driven JSON recipes, custom datapacks, JEI integration, and 1t/craft automation.
 */
public class ExtendedCraftingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final int tier;
    private final int gridSize;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;
    private final Component recipeName;
    private final boolean shapeless;

    // Cached data for fast matching and JEI display
    private List<List<ItemStack>> cachedInputs = null;
    private Map<Item, Integer> cachedRequiredIngredients = null;

    public ExtendedCraftingRecipe(ResourceLocation id, int tier, int gridSize, NonNullList<Ingredient> ingredients,
                                  ItemStack output, @Nullable Component recipeName, boolean shapeless) {
        this.id = id != null ? id : generateDefaultId(tier, output);
        this.tier = tier;
        this.gridSize = gridSize;
        this.ingredients = ingredients;
        this.output = output;
        this.recipeName = recipeName != null ? recipeName : output.getHoverName();
        this.shapeless = shapeless;
    }

    public ExtendedCraftingRecipe(ResourceLocation id, int tier, int gridSize, List<List<ItemStack>> inputs,
                                  ItemStack output, Component recipeName, boolean shapeless) {
        this.id = id != null ? id : generateDefaultId(tier, output);
        this.tier = tier;
        this.gridSize = gridSize;
        this.output = output;
        this.recipeName = recipeName != null ? recipeName : output.getHoverName();
        this.shapeless = shapeless;
        this.cachedInputs = inputs;

        NonNullList<Ingredient> ingList = NonNullList.withSize(gridSize * gridSize, Ingredient.EMPTY);
        for (int i = 0; i < Math.min(inputs.size(), gridSize * gridSize); i++) {
            List<ItemStack> list = inputs.get(i);
            if (list != null && !list.isEmpty()) {
                ingList.set(i, Ingredient.of(list.toArray(new ItemStack[0])));
            }
        }
        this.ingredients = ingList;
    }

    private static ResourceLocation generateDefaultId(int tier, ItemStack output) {
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(output.getItem());
        String path = itemKey != null ? itemKey.getPath() : "unknown_recipe";
        return new ResourceLocation(ExponentialMana.MOD_ID, "extended_crafting/tier" + tier + "/" + path);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public int getTier() {
        return tier;
    }

    public int getGridSize() {
        return gridSize;
    }

    public boolean isShapeless() {
        return shapeless;
    }

    public Component getRecipeName() {
        return recipeName != null ? recipeName : output.getHoverName();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.EXTENDED_CRAFTING.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipeTypes.EXTENDED_CRAFTING.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= gridSize && height >= gridSize;
    }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        // Container matching adapter
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) {
        return output.copy();
    }

    /**
     * Converts ingredients into a list of ItemStacks per slot for JEI and recipe transfer display.
     */
    public List<List<ItemStack>> getInputs() {
        if (cachedInputs == null) {
            List<List<ItemStack>> list = new ArrayList<>(gridSize * gridSize);
            for (Ingredient ing : ingredients) {
                if (ing.isEmpty()) {
                    list.add(Collections.emptyList());
                } else {
                    list.add(Arrays.asList(ing.getItems()));
                }
            }
            cachedInputs = Collections.unmodifiableList(list);
        }
        return cachedInputs;
    }

    /**
     * Returns an unmodifiable map of required item counts for this recipe.
     * Essential for automated crafting and shapeless verification.
     */
    public Map<Item, Integer> getRequiredIngredients() {
        if (cachedRequiredIngredients == null) {
            Map<Item, Integer> map = new LinkedHashMap<>();
            for (Ingredient ing : ingredients) {
                if (!ing.isEmpty()) {
                    ItemStack[] items = ing.getItems();
                    if (items.length > 0) {
                        Item primary = items[0].getItem();
                        map.put(primary, map.getOrDefault(primary, 0) + 1);
                    }
                }
            }
            cachedRequiredIngredients = Collections.unmodifiableMap(map);
        }
        return cachedRequiredIngredients;
    }

    /**
     * Highly optimized recipe matching against crafting grid.
     */
    public boolean matches(IItemHandler grid) {
        int totalSlots = gridSize * gridSize;
        if (grid.getSlots() < totalSlots) {
            return false;
        }

        if (shapeless) {
            Map<Item, Integer> req = getRequiredIngredients();
            Map<Item, Integer> found = new HashMap<>();
            int totalFound = 0;
            int totalReq = 0;
            for (int count : req.values()) {
                totalReq += count;
            }

            for (int i = 0; i < totalSlots; i++) {
                ItemStack inGrid = grid.getStackInSlot(i);
                if (!inGrid.isEmpty()) {
                    found.put(inGrid.getItem(), found.getOrDefault(inGrid.getItem(), 0) + inGrid.getCount());
                    totalFound += inGrid.getCount();
                }
            }

            if (totalFound < totalReq) return false;

            for (Map.Entry<Item, Integer> entry : req.entrySet()) {
                if (found.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                    return false;
                }
            }
            return true;
        }

        if (ingredients.size() < totalSlots) {
            return false;
        }

        for (int i = 0; i < totalSlots; i++) {
            ItemStack inGrid = grid.getStackInSlot(i);
            Ingredient allowed = ingredients.get(i);

            if (allowed.isEmpty()) {
                if (!inGrid.isEmpty()) return false;
            } else {
                if (!allowed.test(inGrid)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Serializer for data-driven JSON recipes.
     */
    public static class Serializer implements RecipeSerializer<ExtendedCraftingRecipe> {
        @Override
        public @NotNull ExtendedCraftingRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            int tier = GsonHelper.getAsInt(json, "tier", 1);
            if (tier < 1 || tier > 10) {
                throw new JsonSyntaxException("Extended Crafting recipe tier must be between 1 and 10! Got: " + tier);
            }
            int gridSize = tier + 3;
            int totalSlots = gridSize * gridSize;

            boolean shapeless = GsonHelper.getAsBoolean(json, "shapeless", false);
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);

            Component name = null;
            if (json.has("name")) {
                name = Component.translatable(GsonHelper.getAsString(json, "name"));
            }

            NonNullList<Ingredient> ingredients = NonNullList.withSize(totalSlots, Ingredient.EMPTY);

            // Shortcut: "full_grid": true, "ingredient": { ... }
            if (GsonHelper.getAsBoolean(json, "full_grid", false)) {
                Ingredient ing = Ingredient.fromJson(json.get("ingredient"));
                for (int i = 0; i < totalSlots; i++) {
                    ingredients.set(i, ing);
                }
                shapeless = true;
                return new ExtendedCraftingRecipe(recipeId, tier, gridSize, ingredients, result, name, shapeless);
            }

            if (shapeless) {
                if (!json.has("ingredients")) {
                    throw new JsonSyntaxException("Shapeless Extended Crafting recipe requires 'ingredients' array!");
                }
                JsonArray ingArray = GsonHelper.getAsJsonArray(json, "ingredients");
                int idx = 0;
                for (JsonElement elem : ingArray) {
                    if (elem.isJsonObject() && elem.getAsJsonObject().has("count")) {
                        JsonObject obj = elem.getAsJsonObject();
                        int count = GsonHelper.getAsInt(obj, "count", 1);
                        Ingredient ing = Ingredient.fromJson(obj);
                        for (int c = 0; c < count && idx < totalSlots; c++) {
                            ingredients.set(idx++, ing);
                        }
                    } else {
                        if (idx < totalSlots) {
                            ingredients.set(idx++, Ingredient.fromJson(elem));
                        }
                    }
                }
            } else {
                // Shaped recipe using pattern & key
                if (!json.has("pattern") || !json.has("key")) {
                    throw new JsonSyntaxException("Shaped Extended Crafting recipe requires 'pattern' and 'key'!");
                }
                JsonArray patternArray = GsonHelper.getAsJsonArray(json, "pattern");
                if (patternArray.size() != gridSize) {
                    throw new JsonSyntaxException("Pattern rows (" + patternArray.size() + ") must match table grid size (" + gridSize + ") for tier " + tier);
                }

                JsonObject keyObj = GsonHelper.getAsJsonObject(json, "key");
                Map<Character, Ingredient> keyMap = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : keyObj.entrySet()) {
                    if (entry.getKey().length() != 1) {
                        throw new JsonSyntaxException("Key symbol must be a single character, got: " + entry.getKey());
                    }
                    keyMap.put(entry.getKey().charAt(0), Ingredient.fromJson(entry.getValue()));
                }

                for (int r = 0; r < gridSize; r++) {
                    String row = patternArray.get(r).getAsString();
                    if (row.length() != gridSize) {
                        throw new JsonSyntaxException("Pattern row " + r + " length (" + row.length() + ") must match grid size (" + gridSize + ") for tier " + tier);
                    }
                    for (int c = 0; c < gridSize; c++) {
                        char ch = row.charAt(c);
                        if (ch != ' ' && ch != '.') {
                            Ingredient ing = keyMap.get(ch);
                            if (ing == null) {
                                throw new JsonSyntaxException("Undefined key symbol in pattern: '" + ch + "'");
                            }
                            ingredients.set(r * gridSize + c, ing);
                        }
                    }
                }
            }

            return new ExtendedCraftingRecipe(recipeId, tier, gridSize, ingredients, result, name, shapeless);
        }

        @Override
        public @Nullable ExtendedCraftingRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buf) {
            int tier = buf.readVarInt();
            boolean shapeless = buf.readBoolean();
            int gridSize = buf.readVarInt();
            int totalSlots = buf.readVarInt();

            NonNullList<Ingredient> ingredients = NonNullList.withSize(totalSlots, Ingredient.EMPTY);
            for (int i = 0; i < totalSlots; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }
            ItemStack output = buf.readItem();
            Component name = buf.readComponent();

            return new ExtendedCraftingRecipe(recipeId, tier, gridSize, ingredients, output, name, shapeless);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull ExtendedCraftingRecipe recipe) {
            buf.writeVarInt(recipe.tier);
            buf.writeBoolean(recipe.shapeless);
            buf.writeVarInt(recipe.gridSize);
            buf.writeVarInt(recipe.ingredients.size());

            for (Ingredient ing : recipe.ingredients) {
                ing.toNetwork(buf);
            }
            buf.writeItem(recipe.output);
            buf.writeComponent(recipe.recipeName != null ? recipe.recipeName : recipe.output.getHoverName());
        }
    }
}
