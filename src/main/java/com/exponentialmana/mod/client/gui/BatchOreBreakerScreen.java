package com.exponentialmana.mod.client.gui;

import com.exponentialmana.mod.block.entity.BatchOreBreakerBlockEntity;
import com.exponentialmana.mod.menu.BatchOreBreakerMenu;
import com.exponentialmana.mod.util.CompactStackFormatter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BatchOreBreakerScreen extends AbstractContainerScreen<BatchOreBreakerMenu> {
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public BatchOreBreakerScreen(BatchOreBreakerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 198;
        this.imageHeight = 290;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SLOT_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Background dark panel with border
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, 0xFF373737);

        // Precise slot drawing synchronized with menu slot positions
        for (Slot slot : menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            guiGraphics.blit(SLOT_TEXTURE, sx, sy, 7, 17, 18, 18);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);

        BatchOreBreakerBlockEntity be = menu.getBlockEntity();
        if (be != null) {
            long total = 0;
            for (long c : be.getManaPool()) total += c;
            String manaStr = "Pool: " + CompactStackFormatter.formatCount(total);
            guiGraphics.drawString(this.font, manaStr, 110, 6, 0x1565C0, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
