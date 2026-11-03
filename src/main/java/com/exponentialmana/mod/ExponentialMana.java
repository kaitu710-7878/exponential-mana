package com.exponentialmana.mod;

import com.exponentialmana.mod.registry.ModBlockEntities;
import com.exponentialmana.mod.registry.ModBlocks;
import com.exponentialmana.mod.registry.ModCreativeModeTabs;
import com.exponentialmana.mod.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Main entry point for Exponential Mana Mod.
 * Target: Minecraft 1.20.1 Forge
 * License: GNU General Public License v3.0 (GPL-3.0)
 */
@Mod(ExponentialMana.MOD_ID)
public class ExponentialMana {
    public static final String MOD_ID = "exponential_mana";
    public static final String VERSION = "0.11.0-alpha";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExponentialMana() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register registries
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        com.exponentialmana.mod.registry.ModMenuTypes.register(modEventBus);
        com.exponentialmana.mod.registry.ModRecipeSerializers.register(modEventBus);

        // Register lifecycle events
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // Register to Forge Event Bus
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Exponential Mana [{}] initialized successfully.", VERSION);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Exponential Mana: Common setup starting...");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Creative tab population hook
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Exponential Mana: Server starting...");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                net.minecraft.client.gui.screens.MenuScreens.register(
                        com.exponentialmana.mod.registry.ModMenuTypes.FAIRY_MENU.get(),
                        com.exponentialmana.mod.client.gui.FairyScreen::new);
                net.minecraft.client.gui.screens.MenuScreens.register(
                        com.exponentialmana.mod.registry.ModMenuTypes.THAWER_MENU.get(),
                        com.exponentialmana.mod.client.gui.ThawerScreen::new);
                net.minecraft.client.gui.screens.MenuScreens.register(
                        com.exponentialmana.mod.registry.ModMenuTypes.BATCH_ORE_BREAKER_MENU.get(),
                        com.exponentialmana.mod.client.gui.BatchOreBreakerScreen::new);
                net.minecraft.client.gui.screens.MenuScreens.register(
                        com.exponentialmana.mod.registry.ModMenuTypes.EXTENDED_CRAFTING_MENU.get(),
                        com.exponentialmana.mod.client.gui.ExtendedCraftingScreen::new);
                net.minecraft.client.gui.screens.MenuScreens.register(
                        com.exponentialmana.mod.registry.ModMenuTypes.FAIRY_ATTRACTOR_MENU.get(),
                        com.exponentialmana.mod.client.gui.FairyAttractorScreen::new);
            });
            LOGGER.info("Exponential Mana: Client GUI screens registered successfully.");
        }
    }
}
