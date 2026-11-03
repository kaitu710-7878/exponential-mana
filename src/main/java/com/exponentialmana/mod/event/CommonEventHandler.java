package com.exponentialmana.mod.event;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.block.InactiveManaOreBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Common event handlers for Exponential Mana.
 */
@Mod.EventBusSubscriber(modid = ExponentialMana.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {

    /**
     * Enforces the "Player Mining Only" rule for Inactive Mana Ore.
     * Prevents automated block breakers, quarry machines, and fake players from breaking inactive ore.
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof InactiveManaOreBlock) {
            Player player = event.getPlayer();
            // Block if broken by non-player or automated fake player
            if (player == null || player instanceof FakePlayer) {
                event.setCanceled(true);
            }
        }
    }

    /**
     * Complete invulnerability for players wearing Origin Armor (blocks all damage including /kill and Void).
     */
    @SubscribeEvent
    public static void onLivingAttack(net.minecraftforge.event.entity.living.LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player && hasOriginArmor(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && hasOriginArmor(player)) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(net.minecraftforge.event.entity.living.LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player && hasOriginArmor(player)) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && hasOriginArmor(player)) {
            event.setCanceled(true);
            player.setHealth(player.getMaxHealth());
        }
    }

    private static boolean hasOriginArmor(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (!armor.isEmpty() && armor.getItem() instanceof com.exponentialmana.mod.item.OriginArmorItem) {
                return true;
            }
        }
        return false;
    }
}
