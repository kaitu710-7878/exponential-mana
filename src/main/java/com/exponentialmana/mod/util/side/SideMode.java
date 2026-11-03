package com.exponentialmana.mod.util.side;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Side configuration modes for machine faces, inspired by Mekanism.
 */
public enum SideMode {
    DISABLED("disabled", 0xFF555555, ChatFormatting.DARK_GRAY), // Closed / blocked
    INPUT("input", 0xFF3388FF, ChatFormatting.BLUE),            // Import only
    OUTPUT("output", 0xFFFFCC00, ChatFormatting.YELLOW),        // Export only (and Auto-eject target)
    BOTH("both", 0xFFAA33FF, ChatFormatting.LIGHT_PURPLE);      // Import and Export (and Auto-eject target)

    private final String name;
    private final int color;
    private final ChatFormatting formatting;

    SideMode(String name, int color, ChatFormatting formatting) {
        this.name = name;
        this.color = color;
        this.formatting = formatting;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }

    public Component getDisplayName() {
        return Component.translatable("gui.exponential_mana.side_mode." + name).withStyle(formatting);
    }

    public SideMode next() {
        return switch (this) {
            case DISABLED -> INPUT;
            case INPUT -> OUTPUT;
            case OUTPUT -> BOTH;
            case BOTH -> DISABLED;
        };
    }
}
