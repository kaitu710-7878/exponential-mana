package com.exponentialmana.mod.client.gui;

import com.exponentialmana.mod.client.gui.widget.SideConfigWidget;
import com.exponentialmana.mod.menu.ExtendedCraftingMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ExtendedCraftingScreen extends AbstractContainerScreen<ExtendedCraftingMenu> {
    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    private SideConfigWidget sideConfigWidget;

    public ExtendedCraftingScreen(ExtendedCraftingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = menu.getImageWidth();
        this.imageHeight = menu.getImageHeight();
    }

    @Override
    protected void init() {
        this.imageWidth = menu.getImageWidth();
        this.imageHeight = menu.getImageHeight();
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // Position SideConfigWidget neatly next to the player inventory (to the right, within imageWidth)
        int widgetX = this.leftPos + 178;
        int widgetY = this.topPos + menu.getPlayerInvY();
        this.sideConfigWidget = new SideConfigWidget(widgetX, widgetY,
                menu.getBlockEntity().getBlockPos(), menu.getBlockEntity());
        this.addRenderableWidget(sideConfigWidget);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;

        // 1. Container background panel (crisp vanilla-style beveled dialog)
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        guiGraphics.fill(x, y, x + imageWidth, y + 1, 0xFFFFFFFF); // Top light highlight
        guiGraphics.fill(x, y, x + 1, y + imageHeight, 0xFFFFFFFF); // Left light highlight
        guiGraphics.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, 0xFF555555); // Right dark shadow
        guiGraphics.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, 0xFF555555); // Bottom dark shadow
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, 0xFF373737); // Perimeter border

        // 2. Draw standard slots (18x18 beveled inset slot frames)
        int manualResultIndex = menu.getManualResultMenuIndex();
        for (int i = 0; i < menu.slots.size(); i++) {
            if (i == manualResultIndex) continue; // Drawn separately with 26x26 big frame
            Slot slot = menu.slots.get(i);
            drawStandardSlotFrame(guiGraphics, x + slot.x - 1, y + slot.y - 1);
        }

        // 3. Draw Crafting Arrow and 26x26 big output frame for manual craft slot
        Slot resultSlot = menu.slots.get(manualResultIndex);
        int outFrameX = x + resultSlot.x - 5;
        int outFrameY = y + resultSlot.y - 5;

        // Crafting Arrow (22x15)
        int arrowX = outFrameX - 26;
        int arrowY = outFrameY + 5;
        guiGraphics.blit(CRAFTING_TEXTURE, arrowX, arrowY, 90, 35, 22, 15);

        // 26x26 big output slot frame
        guiGraphics.blit(CRAFTING_TEXTURE, outFrameX, outFrameY, 119, 30, 26, 26);
    }

    private void drawStandardSlotFrame(GuiGraphics guiGraphics, int sx, int sy) {
        // Standard Minecraft 18x18 recessed slot appearance
        guiGraphics.fill(sx, sy, sx + 18, sy + 18, 0xFF8B8B8B); // Inset slot background
        guiGraphics.fill(sx, sy, sx + 18, sy + 1, 0xFF373737); // Top shadow
        guiGraphics.fill(sx, sy, sx + 1, sy + 18, 0xFF373737); // Left shadow
        guiGraphics.fill(sx + 17, sy + 1, sx + 18, sy + 18, 0xFFFFFFFF); // Right highlight
        guiGraphics.fill(sx + 1, sy + 17, sx + 18, sy + 18, 0xFFFFFFFF); // Bottom highlight
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 1. Table title
        guiGraphics.drawString(this.font, this.title, 10, 8, 0x404040, false);

        // 2. Material Buffer label
        int bufX = menu.getBufferStartX();
        int bufY = menu.getBufferStartY() - 11;
        guiGraphics.drawString(this.font, Component.translatable("gui.exponential_mana.material_buffer"), bufX, bufY, 0x404040, false);

        // 3. Auto-Craft Output label
        int autoX = menu.getAutoOutputStartX();
        int autoY = menu.getAutoOutputStartY() - 11;
        guiGraphics.drawString(this.font, Component.translatable("gui.exponential_mana.auto_output"), autoX, autoY, 0x404040, false);

        // 4. Player Inventory label
        int invX = menu.getPlayerInvX();
        int invY = menu.getPlayerInvY() - 11;
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), invX, invY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Render compact stack text (K, M, B) for slots with >= 1000 items
        // Delegate to BiggerStacks when biggerstacks mod is loaded to prevent duplicate count display
        if (!net.minecraftforge.fml.ModList.get().isLoaded("biggerstacks")) {
            for (Slot slot : menu.slots) {
                if (slot.hasItem()) {
                    int count = slot.getItem().getCount();
                    if (count >= 1000) {
                        String formatted = com.exponentialmana.mod.util.CompactStackFormatter.formatCount(count);
                        int textX = this.leftPos + slot.x + 19 - this.font.width(formatted);
                        int textY = this.topPos + slot.y + 16 - 9;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0.0F, 0.0F, 250.0F);
                        guiGraphics.drawString(this.font, formatted, textX, textY, 0xFFFFFF, true);
                        guiGraphics.pose().popPose();
                    }
                }
            }
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
        if (sideConfigWidget != null) {
            sideConfigWidget.renderTooltips(guiGraphics, mouseX, mouseY);
        }
    }
}
