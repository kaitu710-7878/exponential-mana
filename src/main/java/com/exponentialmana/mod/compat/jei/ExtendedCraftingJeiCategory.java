package com.exponentialmana.mod.compat.jei;

import com.exponentialmana.mod.ExponentialMana;
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

public class ExtendedCraftingJeiCategory implements IRecipeCategory<ExtendedCraftingJeiRecipe> {
    public static final RecipeType<ExtendedCraftingJeiRecipe> TYPE =
            RecipeType.create(ExponentialMana.MOD_ID, "extended_crafting", ExtendedCraftingJeiRecipe.class);

    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public ExtendedCraftingJeiCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 150);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.TIER1_EXTENDED_CRAFTING_TABLE.get()));
        this.slotDrawable = guiHelper.getSlotDrawable();
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

        int maxArea = 112;
        int slotPitch = Math.max(8, maxArea / gridSize);
        int startX = 6 + (maxArea - gridSize * slotPitch) / 2;
        int startY = 24 + (maxArea - gridSize * slotPitch) / 2;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int index = col + row * gridSize;
                if (index < inputs.size()) {
                    List<ItemStack> slotInputs = inputs.get(index);
                    if (slotInputs != null && !slotInputs.isEmpty()) {
                        var slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, startX + col * slotPitch, startY + row * slotPitch)
                                .addItemStacks(slotInputs);
                        if (slotPitch >= 14) {
                            slotBuilder.setBackground(slotDrawable, -1, -1);
                        }
                    }
                }
            }
        }

        // Output Slot at x=142, y=66 (Big output slot)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 66)
                .addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(@NotNull ExtendedCraftingJeiRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        // Container panel background
        guiGraphics.fill(2, 2, 174, 148, 0xFFE0E0E0);
        guiGraphics.renderOutline(2, 2, 172, 146, 0xFF373737);

        // Header: Tier & Grid size title
        Component tierHeader = Component.translatable("jei.exponential_mana.extended_crafting.tier_grid",
                recipe.getTier(), recipe.getGridSize(), recipe.getGridSize());
        guiGraphics.drawString(font, tierHeader, 6, 5, 0x1A237E, false);

        // Recipe Item Name below header
        Component name = recipe.getRecipeName();
        guiGraphics.drawString(font, name, 6, 15, 0x333333, false);

        // Grid Area Outline
        int maxArea = 112;
        int slotPitch = Math.max(8, maxArea / recipe.getGridSize());
        int startX = 6 + (maxArea - recipe.getGridSize() * slotPitch) / 2;
        int startY = 24 + (maxArea - recipe.getGridSize() * slotPitch) / 2;
        int gridPixelSize = recipe.getGridSize() * slotPitch;
        guiGraphics.renderOutline(startX - 1, startY - 1, gridPixelSize + 2, gridPixelSize + 2, 0xFF888888);

        // Crafting Arrow
        guiGraphics.blit(CRAFTING_TEXTURE, 120, 68, 90, 35, 18, 15);

        // Output Slot 26x26 frame
        guiGraphics.blit(CRAFTING_TEXTURE, 137, 61, 119, 30, 26, 26);
    }
}
