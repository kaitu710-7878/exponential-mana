package com.exponentialmana.mod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Origin Armor Item.
 * Grants creative flight and absolute invulnerability.
 */
public class OriginArmorItem extends ArmorItem {
    public OriginArmorItem(Type type) {
        super(ModArmorMaterials.ORIGIN, type, new Properties().rarity(Rarity.EPIC).fireResistant());
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            // Check if equipped in armor slot
            ItemStack equipped = player.getItemBySlot(getEquipmentSlot());
            if (equipped == stack) {
                // Grant flight
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.origin_armor.flight").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.exponential_mana.origin_armor.invulnerable").withStyle(ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
