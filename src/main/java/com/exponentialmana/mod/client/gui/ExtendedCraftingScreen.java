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
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

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

        // Position Side Config Widget on the right side of the screen
        int widgetX = this.leftPos + this.imageWidth + 4;
        int widgetY = this.topPos + 4;
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

        // 1. Container background panel
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        guiGraphics.renderOutline(x, y, imageWidth, imageHeight, 0xFF373737);

        // 2. Draw standard slots (Grid, Buffer, Player Inv, Hotbar)
        int outputSlotIndex = menu.getOutputSlotIndex();
        RenderSystem.setShaderTexture(0, SLOT_TEXTURE);
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (i != outputSlotIndex) {
                guiGraphics.blit(SLOT_TEXTURE, x + slot.x - 1, y + slot.y - 1, 7, 17, 18, 18);
            }
        }

        // 3. Draw 26x26 big output frame
        RenderSystem.setShaderTexture(0, CRAFTING_TEXTURE);
        Slot outputSlot = menu.slots.get(outputSlotIndex);
        int outFrameX = x + outputSlot.x - 5;
        int outFrameY = y + outputSlot.y - 5;
        guiGraphics.blit(CRAFTING_TEXTURE, outFrameX, outFrameY, 119, 30, 26, 26);

        // 4. Draw Crafting Arrow
        int arrowX = outFrameX - 26;
        int arrowY = outFrameY + 5;
        guiGraphics.blit(CRAFTING_TEXTURE, arrowX, arrowY, 90, 35, 22, 15);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Table title
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);

        // Material Buffer label
        int bufX = menu.getBufferStartX();
        int bufY = menu.getBufferStartY() - 9;
        guiGraphics.drawString(this.font, Component.translatable("gui.exponential_mana.material_buffer"), bufX, bufY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (sideConfigWidget != null) {
            sideConfigWidget.renderTooltips(guiGraphics, mouseX, mouseY);
        }
    }
}
