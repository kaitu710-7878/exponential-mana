package com.exponentialmana.mod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Origin Sword Item.
 * Features an internal `/kill` logic to instantly eliminate any struck entity.
 */
public class OriginSwordItem extends SwordItem {
    public OriginSwordItem() {
        super(Tiers.NETHERITE, 999999999, 10.0F,
                new Properties().rarity(Rarity.EPIC).fireResistant());
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Execute /kill instant death logic
        target.setHealth(0.0F);
        target.kill();
        target.discard();
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.origin_sword.kill").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
