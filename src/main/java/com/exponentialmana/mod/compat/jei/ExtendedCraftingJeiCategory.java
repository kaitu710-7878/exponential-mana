package com.exponentialmana.mod.compat.jei;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtendedCraftingJeiCategory implements IRecipeCategory<ExtendedCraftingJeiRecipe> {
    public static final RecipeType<ExtendedCraftingJeiRecipe> TYPE =
            RecipeType.create(ExponentialMana.MOD_ID, "extended_crafting", ExtendedCraftingJeiRecipe.class);

    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    private final IDrawable background;
    private final IDrawable icon;

    public ExtendedCraftingJeiCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 140);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.TIER1_EXTENDED_CRAFTING_TABLE.get()));
    }

    @Override
    public @NotNull RecipeType<ExtendedCraftingJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.exponential_mana.extended_crafting.title");
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ExtendedCraftingJeiRecipe recipe, @NotNull IFocusGroup focuses) {
        int gridSize = recipe.getGridSize();
        List<List<ItemStack>> inputs = recipe.getInputs();

        // Fit matrix in 108x108 space (from x=6, y=20)
        int maxGridWidth = 108;
        int slotPitch = Math.max(8, maxGridWidth / gridSize);
        int startX = 6 + (maxGridWidth - gridSize * slotPitch) / 2;
        int startY = 20 + (maxGridWidth - gridSize * slotPitch) / 2;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int index = col + row * gridSize;
                if (index < inputs.size()) {
                    List<ItemStack> slotInputs = inputs.get(index);
                    if (slotInputs != null && !slotInputs.isEmpty()) {
                        builder.addSlot(RecipeIngredientRole.INPUT, startX + col * slotPitch, startY + row * slotPitch)
                                .addItemStacks(slotInputs);
                    }
                }
            }
        }

        // Output Slot at x=142, y=66
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 66)
                .addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(@NotNull ExtendedCraftingJeiRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw container-style outline for the recipe area
        guiGraphics.fill(2, 2, 174, 138, 0xFFE0E0E0);
        guiGraphics.renderOutline(2, 2, 172, 136, 0xFF373737);

        // Header: Tier & Grid size title
        Component header = Component.translatable("tooltip.exponential_mana.extended_crafting_table.tier",
                recipe.getTier(), recipe.getGridSize(), recipe.getGridSize());
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, header, 6, 6, 0x333333, false);

        // Draw crafting arrow between grid and output slot
        guiGraphics.blit(CRAFTING_TEXTURE, 118, 67, 90, 35, 20, 15);

        // Draw description if present
        if (recipe.getDescriptionKey() != null) {
            Component desc = Component.translatable(recipe.getDescriptionKey());
            guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, desc, 6, 128, 0x666666, false);
        }
    }
}
