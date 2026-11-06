package com.exponentialmana.mod.client.gui;

import com.exponentialmana.mod.client.gui.widget.SideConfigWidget;
import com.exponentialmana.mod.menu.FairyAttractorMenu;
import com.exponentialmana.mod.util.CompactStackFormatter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FairyAttractorScreen extends AbstractContainerScreen<FairyAttractorMenu> {
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    private SideConfigWidget sideConfigWidget;

    public FairyAttractorScreen(FairyAttractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 244;
        this.inventoryLabelY = 18 + 8 * 18 + 3;
    }

    @Override
    protected void init() {
        super.init();
        // Side Config Widget at bottom-right corner
        int widgetX = leftPos + imageWidth - 28;
        int widgetY = topPos + 18 + 8 * 18 + 14;
        this.sideConfigWidget = new SideConfigWidget(widgetX, widgetY, menu.getBlockEntity().getBlockPos(), menu.getBlockEntity());
        this.addRenderableWidget(sideConfigWidget);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;

        // Container background
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, 0xFF373737);

        // Render slots background
        RenderSystem.setShaderTexture(0, SLOT_TEXTURE);
        for (Slot slot : menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            guiGraphics.blit(SLOT_TEXTURE, sx, sy, 7, 17, 18, 18);
        }

        // Draw Timer / Countdown progress text
        int timer = menu.getTimer();
        int totalSec = timer / 20;
        int min = totalSec / 60;
        int sec = totalSec % 60;
        String timeStr = String.format("%d:%02d", min, sec);
        guiGraphics.drawString(this.font, Component.translatable("gui.exponential_mana.fairy_attractor.next_lure", timeStr),
                x + 80, y + 6, 0x404040, false);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Compact stack numbers for >= 1,000 counts
        // Delegate to BiggerStacks when loaded
        if (!net.minecraftforge.fml.ModList.get().isLoaded("biggerstacks")) {
            for (int i = 0; i < FairyAttractorMenu.SLOTS; i++) {
                Slot slot = menu.slots.get(i);
                if (slot.hasItem()) {
                    ItemStack stack = slot.getItem();
                    if (stack.getCount() >= 1000) {
                        String formatted = CompactStackFormatter.formatCount(stack.getCount());
                        int textX = leftPos + slot.x + 19 - this.font.width(formatted);
                        int textY = topPos + slot.y + 16 - 9;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0.0F, 0.0F, 250.0F);
                        guiGraphics.drawString(this.font, formatted, textX, textY, 0xFFFFFF, true);
                        guiGraphics.pose().popPose();
                    }
                }
            }
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
