package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExponentialMana.MOD_ID);

    // ==========================================
    // Tier 0 - 10 Mana
    // ==========================================
    public static final RegistryObject<Item> TIER0_MANA = ITEMS.register("tier0_mana",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER1_MANA = ITEMS.register("tier1_mana",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER2_MANA = ITEMS.register("tier2_mana",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER3_MANA = ITEMS.register("tier3_mana",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER4_MANA = ITEMS.register("tier4_mana",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER5_MANA = ITEMS.register("tier5_mana",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER6_MANA = ITEMS.register("tier6_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER7_MANA = ITEMS.register("tier7_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER8_MANA = ITEMS.register("tier8_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER9_MANA = ITEMS.register("tier9_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER10_MANA = ITEMS.register("tier10_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    // ==========================================
    // Tier 1 - 10 Cores
    // ==========================================
    public static final RegistryObject<Item> TIER1_CORE = ITEMS.register("tier1_core",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER2_CORE = ITEMS.register("tier2_core",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER3_CORE = ITEMS.register("tier3_core",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER4_CORE = ITEMS.register("tier4_core",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER5_CORE = ITEMS.register("tier5_core",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER6_CORE = ITEMS.register("tier6_core",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER7_CORE = ITEMS.register("tier7_core",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER8_CORE = ITEMS.register("tier8_core",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER9_CORE = ITEMS.register("tier9_core",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER10_CORE = ITEMS.register("tier10_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    // ==========================================
    // Fairy Catalyst System
    // ==========================================
    public static final RegistryObject<Item> FAIRY_RESIDUE = ITEMS.register("fairy_residue",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER1_FAIRY_CATALYST = ITEMS.register("tier1_fairy_catalyst",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER2_FAIRY_CATALYST = ITEMS.register("tier2_fairy_catalyst",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER3_FAIRY_CATALYST = ITEMS.register("tier3_fairy_catalyst",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER4_FAIRY_CATALYST = ITEMS.register("tier4_fairy_catalyst",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER5_FAIRY_CATALYST = ITEMS.register("tier5_fairy_catalyst",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER6_FAIRY_CATALYST = ITEMS.register("tier6_fairy_catalyst",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER7_FAIRY_CATALYST = ITEMS.register("tier7_fairy_catalyst",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER8_FAIRY_CATALYST = ITEMS.register("tier8_fairy_catalyst",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER9_FAIRY_CATALYST = ITEMS.register("tier9_fairy_catalyst",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER10_FAIRY_CATALYST = ITEMS.register("tier10_fairy_catalyst",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> PURE_FAIRY_CATALYST = ITEMS.register("pure_fairy_catalyst",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    // ==========================================
    // Deconstruction Shards (for Even Tier Upgrades)
    // ==========================================
    // Ore Shards
    public static final RegistryObject<Item> TIER1_MANA_ORE_SHARD = ITEMS.register("tier1_mana_ore_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER3_MANA_ORE_SHARD = ITEMS.register("tier3_mana_ore_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER5_MANA_ORE_SHARD = ITEMS.register("tier5_mana_ore_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER7_MANA_ORE_SHARD = ITEMS.register("tier7_mana_ore_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER9_MANA_ORE_SHARD = ITEMS.register("tier9_mana_ore_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    // Catalyst Shards
    public static final RegistryObject<Item> TIER1_FAIRY_CATALYST_SHARD = ITEMS.register("tier1_fairy_catalyst_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER3_FAIRY_CATALYST_SHARD = ITEMS.register("tier3_fairy_catalyst_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER5_FAIRY_CATALYST_SHARD = ITEMS.register("tier5_fairy_catalyst_shard",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER7_FAIRY_CATALYST_SHARD = ITEMS.register("tier7_fairy_catalyst_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER9_FAIRY_CATALYST_SHARD = ITEMS.register("tier9_fairy_catalyst_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    // Mini Crafting Tables
    public static final RegistryObject<Item> TIER1_MINI_CRAFTING_TABLE = ITEMS.register("tier1_mini_crafting_table",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER3_MINI_CRAFTING_TABLE = ITEMS.register("tier3_mini_crafting_table",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER5_MINI_CRAFTING_TABLE = ITEMS.register("tier5_mini_crafting_table",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER7_MINI_CRAFTING_TABLE = ITEMS.register("tier7_mini_crafting_table",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER9_MINI_CRAFTING_TABLE = ITEMS.register("tier9_mini_crafting_table",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    // ==========================================
    // Endgame Items & Root Materials
    // ==========================================
    public static final RegistryObject<Item> PURE_MANA = ITEMS.register("pure_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ORIGIN_ALLOY = ITEMS.register("origin_alloy",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> COMPRESSED_ORIGIN_ALLOY = ITEMS.register("compressed_origin_alloy",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> MANA_KNOWLEDGE = ITEMS.register("mana_knowledge",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> MANA_WISDOM = ITEMS.register("mana_wisdom",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    // 4 Root Manas
    public static final RegistryObject<Item> UNCOMPRESSED_STAR_MANA = ITEMS.register("uncompressed_star_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> STAR_MANA = ITEMS.register("star_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> UNCOMPRESSED_QUANTUM_MANA = ITEMS.register("uncompressed_quantum_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> QUANTUM_MANA = ITEMS.register("quantum_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> UNCOMPRESSED_ABYSSAL_MANA = ITEMS.register("uncompressed_abyssal_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ABYSSAL_MANA = ITEMS.register("abyssal_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> UNCOMPRESSED_MARINE_MANA = ITEMS.register("uncompressed_marine_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> MARINE_MANA = ITEMS.register("marine_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> MANA_ROOT = ITEMS.register("mana_root",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> INFINITE_MANA = ITEMS.register("infinite_mana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    // Batch Compressed Mana
    public static final RegistryObject<Item> BATCH_COMPRESSED_MANA = ITEMS.register("batch_compressed_mana",
            com.exponentialmana.mod.item.BatchCompressedManaItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
