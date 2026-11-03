package com.exponentialmana.mod.client.gui;

import com.exponentialmana.mod.block.entity.FairyBlockEntity;
import com.exponentialmana.mod.menu.FairyMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class FairyScreen extends AbstractContainerScreen<FairyMenu> {
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    public FairyScreen(FairyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 340;
        this.imageHeight = 262;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Container background
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, 0xFF373737);

        // Synchronized slot background rendering
        RenderSystem.setShaderTexture(0, SLOT_TEXTURE);
        for (Slot slot : menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            guiGraphics.blit(SLOT_TEXTURE, sx, sy, 7, 17, 18, 18);
        }

        // Draw middle arrow
        RenderSystem.setShaderTexture(0, CRAFTING_TEXTURE);
        int arrowX = x + 158;
        int arrowY = y + 80;
        guiGraphics.blit(CRAFTING_TEXTURE, arrowX, arrowY, 90, 35, 22, 15);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 12, 6, 0x404040, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.exponential_mana.catalysts"), 12, 18, 0x555555, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.exponential_mana.outputs"), 184, 18, 0x555555, false);

        FairyBlockEntity be = menu.getBlockEntity();
        if (be != null) {
            int seconds = be.getCooldown() / 20;
            String timeStr = seconds + "s";
            guiGraphics.drawString(this.font, timeStr, 158, 100, 0x1565C0, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
