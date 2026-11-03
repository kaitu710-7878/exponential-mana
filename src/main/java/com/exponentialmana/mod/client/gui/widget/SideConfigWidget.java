package com.exponentialmana.mod.client.gui.widget;

import com.exponentialmana.mod.network.ModNetwork;
import com.exponentialmana.mod.network.PacketToggleSideConfig;
import com.exponentialmana.mod.util.side.ISidedMachine;
import com.exponentialmana.mod.util.side.SideConfig;
import com.exponentialmana.mod.util.side.SideMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mekanism-style Side Configuration & Auto-Eject controller widget for machine screens.
 */
public class SideConfigWidget extends AbstractWidget {
    private static final Direction[] SIDES = {
            Direction.UP, Direction.DOWN,
            Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST
    };

    private static final String[] SIDE_NAMES = { "U", "D", "N", "S", "W", "E" };

    private final BlockPos pos;
    private final ISidedMachine machine;

    // Button layout: 6 side buttons + 1 auto-eject button
    // Each side button is 14x14
    private final int buttonSize = 14;

    public SideConfigWidget(int x, int y, BlockPos pos, ISidedMachine machine) {
        super(x, y, 70, 48, Component.empty());
        this.pos = pos;
        this.machine = machine;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        SideConfig config = machine.getSideConfig();
        Font font = Minecraft.getInstance().font;

        // Panel background
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xD0222222);
        guiGraphics.renderOutline(getX(), getY(), width, height, 0xFF444444);

        // Header
        guiGraphics.drawString(font, Component.translatable("gui.exponential_mana.side_config.title"),
                getX() + 3, getY() + 3, 0xAAAAAA, false);

        // 6 Side Buttons (2 rows of 3)
        // Row 1: U, N, D
        // Row 2: W, S, E
        int btnStartX = getX() + 4;
        int btnStartY = getY() + 14;

        for (int i = 0; i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            int bx = btnStartX + col * (buttonSize + 3);
            int by = btnStartY + row * (buttonSize + 2);

            Direction dir = SIDES[i];
            SideMode mode = config.getMode(dir);

            // Button body
            guiGraphics.fill(bx, by, bx + buttonSize, by + buttonSize, mode.getColor() | 0xFF000000);
            guiGraphics.renderOutline(bx, by, buttonSize, buttonSize, 0xFFFFFFFF);

            // Letter (U, D, N, S, W, E)
            String label = SIDE_NAMES[i];
            int labelWidth = font.width(label);
            guiGraphics.drawString(font, label, bx + (buttonSize - labelWidth) / 2, by + 3, 0xFFFFFF, true);
        }

        // Auto-eject toggle button on the right/bottom
        int ejX = btnStartX + 3 * (buttonSize + 3) + 2;
        int ejY = btnStartY + 2;
        boolean eject = config.isAutoEject();
        int ejColor = eject ? 0xFF00CC44 : 0xFFCC0000;
        guiGraphics.fill(ejX, ejY, ejX + 14, ejY + 26, ejColor);
        guiGraphics.renderOutline(ejX, ejY, 14, 26, 0xFFFFFFFF);

        guiGraphics.drawString(font, "E", ejX + 4, ejY + 4, 0xFFFFFF, true);
        guiGraphics.drawString(font, "J", ejX + 4, ejY + 14, 0xFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !isValidClickButton(button)) return false;

        SideConfig config = machine.getSideConfig();
        int btnStartX = getX() + 4;
        int btnStartY = getY() + 14;

        // Check 6 side buttons
        for (int i = 0; i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            int bx = btnStartX + col * (buttonSize + 3);
            int by = btnStartY + row * (buttonSize + 2);

            if (mouseX >= bx && mouseX < bx + buttonSize && mouseY >= by && mouseY < by + buttonSize) {
                Direction dir = SIDES[i];
                config.cycleMode(dir);
                ModNetwork.sendToServer(new PacketToggleSideConfig(pos, dir));
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
        }

        // Check Auto-Eject button
        int ejX = btnStartX + 3 * (buttonSize + 3) + 2;
        int ejY = btnStartY + 2;
        if (mouseX >= ejX && mouseX < ejX + 14 && mouseY >= ejY && mouseY < ejY + 26) {
            config.toggleAutoEject();
            ModNetwork.sendToServer(new PacketToggleSideConfig(pos));
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        return false;
    }

    public void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!isHovered) return;
        SideConfig config = machine.getSideConfig();
        int btnStartX = getX() + 4;
        int btnStartY = getY() + 14;

        // Tooltips for 6 buttons
        for (int i = 0; i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            int bx = btnStartX + col * (buttonSize + 3);
            int by = btnStartY + row * (buttonSize + 2);

            if (mouseX >= bx && mouseX < bx + buttonSize && mouseY >= by && mouseY < by + buttonSize) {
                Direction dir = SIDES[i];
                SideMode mode = config.getMode(dir);
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable("gui.exponential_mana.side." + dir.getSerializedName())
                        .append(": ")
                        .append(mode.getDisplayName()));
                tooltip.add(Component.translatable("gui.exponential_mana.side.click_to_cycle"));
                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
                return;
            }
        }

        // Tooltip for Auto-Eject button
        int ejX = btnStartX + 3 * (buttonSize + 3) + 2;
        int ejY = btnStartY + 2;
        if (mouseX >= ejX && mouseX < ejX + 14 && mouseY >= ejY && mouseY < ejY + 26) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.exponential_mana.auto_eject")
                    .append(": ")
                    .append(config.isAutoEject() ?
                            Component.translatable("gui.exponential_mana.on").withStyle(net.minecraft.ChatFormatting.GREEN) :
                            Component.translatable("gui.exponential_mana.off").withStyle(net.minecraft.ChatFormatting.RED)));
            tooltip.add(Component.translatable("gui.exponential_mana.auto_eject.desc"));
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
