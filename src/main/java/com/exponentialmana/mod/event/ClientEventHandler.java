package com.exponentialmana.mod.event;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.util.CompactStackFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client event handlers for Exponential Mana.
 * Handles large stack tooltips and formatting.
 */
@Mod.EventBusSubscriber(modid = ExponentialMana.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    /**
     * Shows the exact count in the tooltip when hovering over large stacks.
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getCount() >= 1000) {
            String exactFormatted = String.format("%,d", stack.getCount());
            String compactFormatted = CompactStackFormatter.formatCount(stack.getCount());

            event.getToolTip().add(Component.translatable("tooltip.exponential_mana.large_stack_info",
                    exactFormatted, compactFormatted).withStyle(ChatFormatting.GOLD));
        }
    }
}
