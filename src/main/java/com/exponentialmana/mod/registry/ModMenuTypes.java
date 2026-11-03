package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.menu.BatchOreBreakerMenu;
import com.exponentialmana.mod.menu.ExtendedCraftingMenu;
import com.exponentialmana.mod.menu.FairyMenu;
import com.exponentialmana.mod.menu.ThawerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ExponentialMana.MOD_ID);

    public static final RegistryObject<MenuType<FairyMenu>> FAIRY_MENU =
            MENUS.register("fairy_menu", () -> IForgeMenuType.create(FairyMenu::new));

    public static final RegistryObject<MenuType<ThawerMenu>> THAWER_MENU =
            MENUS.register("thawer_menu", () -> IForgeMenuType.create(ThawerMenu::new));

    public static final RegistryObject<MenuType<BatchOreBreakerMenu>> BATCH_ORE_BREAKER_MENU =
            MENUS.register("batch_ore_breaker_menu", () -> IForgeMenuType.create(BatchOreBreakerMenu::new));

    public static final RegistryObject<MenuType<ExtendedCraftingMenu>> EXTENDED_CRAFTING_MENU =
            MENUS.register("extended_crafting_menu", () -> IForgeMenuType.create(ExtendedCraftingMenu::new));

    public static final RegistryObject<MenuType<com.exponentialmana.mod.menu.FairyAttractorMenu>> FAIRY_ATTRACTOR_MENU =
            MENUS.register("fairy_attractor_menu", () -> IForgeMenuType.create(com.exponentialmana.mod.menu.FairyAttractorMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
