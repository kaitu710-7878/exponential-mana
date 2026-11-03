package com.exponentialmana.mod.client.gui;

import com.exponentialmana.mod.menu.ExtendedCraftingMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ExtendedCraftingScreen extends AbstractContainerScreen<ExtendedCraftingMenu> {
    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    private final int gridSize;

    public ExtendedCraftingScreen(ExtendedCraftingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.gridSize = menu.getGridSize();
        int slotSize = (gridSize <= 7) ? 18 : (gridSize <= 9) ? 16 : 14;
        this.imageWidth = Math.max(176, gridSize * slotSize + 60);
        this.imageHeight = Math.max(166, gridSize * slotSize + 110);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CRAFTING_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Container background
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, 0xFF373737);

        int slotSize = (gridSize <= 7) ? 18 : (gridSize <= 9) ? 16 : 14;

        // Draw input matrix slot backgrounds
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int sx = x + 7 + col * slotSize;
                int sy = y + 17 + row * slotSize;
                guiGraphics.blit(CRAFTING_TEXTURE, sx, sy, 29, 16, 18, 18);
            }
        }

        // Draw output slot background
        int outputX = x + 8 + gridSize * slotSize + 19;
        int outputY = y + 17 + (gridSize * slotSize) / 2 - 9;
        guiGraphics.blit(CRAFTING_TEXTURE, outputX, outputY, 123, 34, 26, 26);

        // Player Inventory
        int playerInvY = y + Math.max(17 + gridSize * slotSize + 14, 139);
        int playerInvX = x + Math.max(7, (imageWidth - 162) / 2);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int sx = playerInvX + col * 18;
                int sy = playerInvY + row * 18;
                guiGraphics.blit(CRAFTING_TEXTURE, sx, sy, 29, 16, 18, 18);
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            int sx = playerInvX + col * 18;
            int sy = playerInvY + 58;
            guiGraphics.blit(CRAFTING_TEXTURE, sx, sy, 29, 16, 18, 18);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
