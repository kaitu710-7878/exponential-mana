package com.exponentialmana.mod.compat.jei;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.recipe.ExtendedCraftingRecipe;
import com.exponentialmana.mod.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtendedCraftingJeiCategory implements IRecipeCategory<ExtendedCraftingRecipe> {
    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    private final int tier;
    private final int gridSize;
    private final RecipeType<ExtendedCraftingRecipe> recipeType;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;
    private final int width;
    private final int height;

    public ExtendedCraftingJeiCategory(int tier, IGuiHelper guiHelper) {
        this.tier = tier;
        this.gridSize = tier + 3; // Tier 1: 4, ..., Tier 10: 13
        this.recipeType = RecipeType.create(ExponentialMana.MOD_ID, "extended_crafting_tier_" + tier, ExtendedCraftingRecipe.class);

        int gridPixels = gridSize * 18;
        // Ample width (grid + 120px) to ensure output slot and transfer button never overlap with JEI bookmark/favorite/config widgets on the top-right
        this.width = Math.max(220, gridPixels + 120);
        this.height = Math.max(110, gridPixels + 32);

        this.background = guiHelper.createBlankDrawable(width, height);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.EXTENDED_CRAFTING_TABLES.get(tier - 1).get()));
        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    public int getTier() {
        return tier;
    }

    public int getGridSize() {
        return gridSize;
    }

    @Override
    public @NotNull RecipeType<ExtendedCraftingRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.exponential_mana.extended_crafting.tier_title", tier, gridSize, gridSize);
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ExtendedCraftingRecipe recipe, @NotNull IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getInputs();

        int startX = 8;
        int startY = 24;

        // 18x18 pitch for all slots (standard Extended Crafting Reforked layout)
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int index = col + row * gridSize;
                var slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, startX + col * 18, startY + row * 18)
                        .setBackground(slotDrawable, -1, -1);

                if (index < inputs.size()) {
                    List<ItemStack> slotInputs = inputs.get(index);
                    if (slotInputs != null && !slotInputs.isEmpty()) {
                        slotBuilder.addItemStacks(slotInputs);
                    }
                }
            }
        }

        // Output Slot placed neatly next to arrow, leaving 50px+ margin on the right for JEI bookmark widgets
        int arrowX = startX + gridSize * 18 + 14;
        int outFrameX = arrowX + 28;
        int outX = outFrameX + 5;
        int outY = startY + (gridSize * 18) / 2 - 8;

        builder.addSlot(RecipeIngredientRole.OUTPUT, outX, outY)
                .addItemStack(recipe.getOutput())
                .setBackground(slotDrawable, -1, -1);

        // Move Recipe Transfer ("+") button cleanly beneath the output slot
        builder.moveRecipeTransferButton(outX, outY + 28);
    }

    @Override
    public void draw(@NotNull ExtendedCraftingRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        // Background panel outline
        guiGraphics.fill(2, 2, width - 2, height - 2, 0xFFE0E0E0);
        guiGraphics.renderOutline(2, 2, width - 4, height - 4, 0xFF373737);

        // Header: Tier title and recipe item name
        Component title = Component.translatable("jei.exponential_mana.extended_crafting.tier_grid", tier, gridSize, gridSize);
        guiGraphics.drawString(font, title, 8, 6, 0x1A237E, false);

        Component name = recipe.getRecipeName();
        if (recipe.isShapeless()) {
            guiGraphics.drawString(font, Component.literal("").append(name).append(" §7(不定形 / Shapeless)"), 8, 15, 0x333333, false);
        } else {
            guiGraphics.drawString(font, name, 8, 15, 0x333333, false);
        }

        // Crafting Arrow
        int startX = 8;
        int startY = 24;
        int arrowX = startX + gridSize * 18 + 14;
        int arrowY = startY + (gridSize * 18) / 2 - 7;
        guiGraphics.blit(CRAFTING_TEXTURE, arrowX, arrowY, 90, 35, 22, 15);

        // Big 26x26 output slot frame around the output slot
        int outFrameX = arrowX + 28;
        int outFrameY = startY + (gridSize * 18) / 2 - 13;
        guiGraphics.blit(CRAFTING_TEXTURE, outFrameX, outFrameY, 119, 30, 26, 26);
    }

    @Override
    public ResourceLocation getRegistryName(@NotNull ExtendedCraftingRecipe recipe) {
        return recipe.getId();
    }
}
