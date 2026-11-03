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

    // ==========================================
    // Origin Equipment (Invulnerability & /kill logic)
    // ==========================================
    public static final RegistryObject<Item> ORIGIN_HELMET = ITEMS.register("origin_helmet",
            () -> new com.exponentialmana.mod.item.OriginArmorItem(net.minecraft.world.item.ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> ORIGIN_CHESTPLATE = ITEMS.register("origin_chestplate",
            () -> new com.exponentialmana.mod.item.OriginArmorItem(net.minecraft.world.item.ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> ORIGIN_LEGGINGS = ITEMS.register("origin_leggings",
            () -> new com.exponentialmana.mod.item.OriginArmorItem(net.minecraft.world.item.ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> ORIGIN_BOOTS = ITEMS.register("origin_boots",
            () -> new com.exponentialmana.mod.item.OriginArmorItem(net.minecraft.world.item.ArmorItem.Type.BOOTS));
    public static final RegistryObject<Item> ORIGIN_SWORD = ITEMS.register("origin_sword",
            com.exponentialmana.mod.item.OriginSwordItem::new);

    // ==========================================
    // Mana Materials & Compressed Materials
    // ==========================================
    public static final RegistryObject<Item> TIER1_MANA_MATERIAL = ITEMS.register("tier1_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER2_MANA_MATERIAL = ITEMS.register("tier2_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER3_MANA_MATERIAL = ITEMS.register("tier3_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER4_MANA_MATERIAL = ITEMS.register("tier4_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER5_MANA_MATERIAL = ITEMS.register("tier5_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIER6_MANA_MATERIAL = ITEMS.register("tier6_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER7_MANA_MATERIAL = ITEMS.register("tier7_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TIER8_MANA_MATERIAL = ITEMS.register("tier8_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER9_MANA_MATERIAL = ITEMS.register("tier9_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TIER10_MANA_MATERIAL = ITEMS.register("tier10_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> COMPRESSED_TIER1_MANA_MATERIAL = ITEMS.register("compressed_tier1_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPRESSED_TIER2_MANA_MATERIAL = ITEMS.register("compressed_tier2_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPRESSED_TIER3_MANA_MATERIAL = ITEMS.register("compressed_tier3_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPRESSED_TIER4_MANA_MATERIAL = ITEMS.register("compressed_tier4_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPRESSED_TIER5_MANA_MATERIAL = ITEMS.register("compressed_tier5_mana_material",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPRESSED_TIER6_MANA_MATERIAL = ITEMS.register("compressed_tier6_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> COMPRESSED_TIER7_MANA_MATERIAL = ITEMS.register("compressed_tier7_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> COMPRESSED_TIER8_MANA_MATERIAL = ITEMS.register("compressed_tier8_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> COMPRESSED_TIER9_MANA_MATERIAL = ITEMS.register("compressed_tier9_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> COMPRESSED_TIER10_MANA_MATERIAL = ITEMS.register("compressed_tier10_mana_material",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    // Compressed Additional Minerals
    public static final RegistryObject<Item> COMPRESSED_LAPIS = ITEMS.register("compressed_lapis",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> COMPRESSED_AMETHYST = ITEMS.register("compressed_amethyst",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> COMPRESSED_QUARTZ = ITEMS.register("compressed_quartz",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    // ==========================================
    // Mana Symbols (Tier 0 to 10)
    // ==========================================
    public static final RegistryObject<Item> MANA_SYMBOL_TIER0 = ITEMS.register("mana_symbol_tier0",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER1 = ITEMS.register("mana_symbol_tier1",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER2 = ITEMS.register("mana_symbol_tier2",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER3 = ITEMS.register("mana_symbol_tier3",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER4 = ITEMS.register("mana_symbol_tier4",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER5 = ITEMS.register("mana_symbol_tier5",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER6 = ITEMS.register("mana_symbol_tier6",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER7 = ITEMS.register("mana_symbol_tier7",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER8 = ITEMS.register("mana_symbol_tier8",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER9 = ITEMS.register("mana_symbol_tier9",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> MANA_SYMBOL_TIER10 = ITEMS.register("mana_symbol_tier10",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
