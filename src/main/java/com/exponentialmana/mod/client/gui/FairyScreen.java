package com.exponentialmana.mod.client.gui;

import com.exponentialmana.mod.block.entity.FairyBlockEntity;
import com.exponentialmana.mod.menu.FairyMenu;
import com.exponentialmana.mod.util.CompactStackFormatter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class FairyScreen extends AbstractContainerScreen<FairyMenu> {
    // Authentic vanilla dispenser texture
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/dispenser.png");

    public FairyScreen(FairyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Draw progress arrow / indicator in center (between slot 0 and 1)
        FairyBlockEntity be = menu.getBlockEntity();
        if (be != null) {
            int progress = Math.max(0, FairyBlockEntity.BASE_COOLDOWN - be.getCooldown());
            int arrowWidth = (progress * 24) / FairyBlockEntity.BASE_COOLDOWN;
            // Draw a subtle cyan mana progress indicator bar
            guiGraphics.fill(x + 78, y + 39, x + 78 + arrowWidth, y + 43, 0xFF00E5FF);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        FairyBlockEntity be = menu.getBlockEntity();
        if (be != null) {
            ItemStack catalyst = be.getItemHandler().getStackInSlot(0);
            String statusText;
            if (catalyst.isEmpty()) {
                statusText = "Tier 0 (No Catalyst)";
            } else {
                statusText = "x" + CompactStackFormatter.formatCount(catalyst.getCount());
            }
            guiGraphics.drawString(this.font, statusText, 40, 56, 0x2E7D32, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
