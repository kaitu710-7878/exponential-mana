package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.block.InactiveManaOreBlock;
import com.exponentialmana.mod.block.ManaOreBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExponentialMana.MOD_ID);

    // --- Active Mana Ores ---
    // Tier 0: Normal stone hardness (3.0F), directly drops Tier 0 Mana (no inactive ore drop)
    public static final RegistryObject<Block> TIER0_MANA_ORE = registerBlock("tier0_mana_ore",
            () -> new ManaOreBlock(0, 3.0F, 3.0F, () -> new ItemStack(ModItems.TIER0_MANA.get()), null));

    // Tier 1 to 10 Mana Ores: Drops Inactive Mana Ore + Mana
    public static final RegistryObject<Block> TIER1_MANA_ORE = registerBlock("tier1_mana_ore",
            () -> new ManaOreBlock(1, 4.0F, 4.0F, () -> new ItemStack(ModItems.TIER1_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER1_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER2_MANA_ORE = registerBlock("tier2_mana_ore",
            () -> new ManaOreBlock(2, 5.0F, 5.0F, () -> new ItemStack(ModItems.TIER2_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER2_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER3_MANA_ORE = registerBlock("tier3_mana_ore",
            () -> new ManaOreBlock(3, 6.0F, 6.0F, () -> new ItemStack(ModItems.TIER3_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER3_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER4_MANA_ORE = registerBlock("tier4_mana_ore",
            () -> new ManaOreBlock(4, 7.0F, 7.0F, () -> new ItemStack(ModItems.TIER4_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER4_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER5_MANA_ORE = registerBlock("tier5_mana_ore",
            () -> new ManaOreBlock(5, 8.0F, 8.0F, () -> new ItemStack(ModItems.TIER5_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER5_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER6_MANA_ORE = registerBlock("tier6_mana_ore",
            () -> new ManaOreBlock(6, 10.0F, 10.0F, () -> new ItemStack(ModItems.TIER6_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER6_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER7_MANA_ORE = registerBlock("tier7_mana_ore",
            () -> new ManaOreBlock(7, 12.0F, 12.0F, () -> new ItemStack(ModItems.TIER7_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER7_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER8_MANA_ORE = registerBlock("tier8_mana_ore",
            () -> new ManaOreBlock(8, 15.0F, 15.0F, () -> new ItemStack(ModItems.TIER8_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER8_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER9_MANA_ORE = registerBlock("tier9_mana_ore",
            () -> new ManaOreBlock(9, 20.0F, 20.0F, () -> new ItemStack(ModItems.TIER9_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER9_MANA_ORE.get())));

    public static final RegistryObject<Block> TIER10_MANA_ORE = registerBlock("tier10_mana_ore",
            () -> new ManaOreBlock(10, 30.0F, 30.0F, () -> new ItemStack(ModItems.TIER10_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_TIER10_MANA_ORE.get())));

    // Pure Mana Ore
    public static final RegistryObject<Block> PURE_MANA_ORE = registerBlock("pure_mana_ore",
            () -> new ManaOreBlock(11, 40.0F, 100.0F, () -> new ItemStack(ModItems.PURE_MANA.get()),
                    () -> new ItemStack(ModBlocks.INACTIVE_PURE_MANA_ORE.get())));

    // --- Inactive Mana Ores (Obsidian-level hardness: 50.0F, takes 5 minutes to activate) ---
    public static final RegistryObject<Block> INACTIVE_TIER0_MANA_ORE = registerBlock("inactive_tier0_mana_ore",
            () -> new InactiveManaOreBlock(0, TIER0_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER1_MANA_ORE = registerBlock("inactive_tier1_mana_ore",
            () -> new InactiveManaOreBlock(1, TIER1_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER2_MANA_ORE = registerBlock("inactive_tier2_mana_ore",
            () -> new InactiveManaOreBlock(2, TIER2_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER3_MANA_ORE = registerBlock("inactive_tier3_mana_ore",
            () -> new InactiveManaOreBlock(3, TIER3_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER4_MANA_ORE = registerBlock("inactive_tier4_mana_ore",
            () -> new InactiveManaOreBlock(4, TIER4_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER5_MANA_ORE = registerBlock("inactive_tier5_mana_ore",
            () -> new InactiveManaOreBlock(5, TIER5_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER6_MANA_ORE = registerBlock("inactive_tier6_mana_ore",
            () -> new InactiveManaOreBlock(6, TIER6_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER7_MANA_ORE = registerBlock("inactive_tier7_mana_ore",
            () -> new InactiveManaOreBlock(7, TIER7_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER8_MANA_ORE = registerBlock("inactive_tier8_mana_ore",
            () -> new InactiveManaOreBlock(8, TIER8_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER9_MANA_ORE = registerBlock("inactive_tier9_mana_ore",
            () -> new InactiveManaOreBlock(9, TIER9_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_TIER10_MANA_ORE = registerBlock("inactive_tier10_mana_ore",
            () -> new InactiveManaOreBlock(10, TIER10_MANA_ORE::get));

    public static final RegistryObject<Block> INACTIVE_PURE_MANA_ORE = registerBlock("inactive_pure_mana_ore",
            () -> new InactiveManaOreBlock(11, PURE_MANA_ORE::get));

    // --- Fairy Blocks & Compressed Fairies (9^1 to 9^10) ---
    public static final RegistryObject<Block> FAIRY_BLOCK = registerCustomBlock("fairy_block",
            () -> new com.exponentialmana.mod.block.FairyBlock(0),
            block -> new com.exponentialmana.mod.item.FairyBlockItem(block, new Item.Properties()));

    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_1 = registerBlock("compressed_fairy_block_1",
            () -> new com.exponentialmana.mod.block.FairyBlock(1));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_2 = registerBlock("compressed_fairy_block_2",
            () -> new com.exponentialmana.mod.block.FairyBlock(2));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_3 = registerBlock("compressed_fairy_block_3",
            () -> new com.exponentialmana.mod.block.FairyBlock(3));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_4 = registerBlock("compressed_fairy_block_4",
            () -> new com.exponentialmana.mod.block.FairyBlock(4));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_5 = registerBlock("compressed_fairy_block_5",
            () -> new com.exponentialmana.mod.block.FairyBlock(5));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_6 = registerBlock("compressed_fairy_block_6",
            () -> new com.exponentialmana.mod.block.FairyBlock(6));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_7 = registerBlock("compressed_fairy_block_7",
            () -> new com.exponentialmana.mod.block.FairyBlock(7));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_8 = registerBlock("compressed_fairy_block_8",
            () -> new com.exponentialmana.mod.block.FairyBlock(8));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_9 = registerBlock("compressed_fairy_block_9",
            () -> new com.exponentialmana.mod.block.FairyBlock(9));
    public static final RegistryObject<Block> COMPRESSED_FAIRY_BLOCK_10 = registerBlock("compressed_fairy_block_10",
            () -> new com.exponentialmana.mod.block.FairyBlock(10));

    public static final java.util.List<RegistryObject<Block>> COMPRESSED_FAIRY_BLOCKS = java.util.List.of(
            COMPRESSED_FAIRY_BLOCK_1, COMPRESSED_FAIRY_BLOCK_2, COMPRESSED_FAIRY_BLOCK_3,
            COMPRESSED_FAIRY_BLOCK_4, COMPRESSED_FAIRY_BLOCK_5, COMPRESSED_FAIRY_BLOCK_6,
            COMPRESSED_FAIRY_BLOCK_7, COMPRESSED_FAIRY_BLOCK_8, COMPRESSED_FAIRY_BLOCK_9,
            COMPRESSED_FAIRY_BLOCK_10
    );

    // Fairy Attractor (Lures/generates a fairy every 5 minutes)
    public static final RegistryObject<Block> FAIRY_ATTRACTOR = registerBlock("fairy_attractor",
            com.exponentialmana.mod.block.FairyAttractorBlock::new);

    // Batch Ore Breaker & Thawer
    public static final RegistryObject<Block> BATCH_ORE_BREAKER = registerBlock("batch_ore_breaker",
            com.exponentialmana.mod.block.BatchOreBreakerBlock::new);
    public static final RegistryObject<Block> THAWER = registerBlock("thawer",
            com.exponentialmana.mod.block.ThawerBlock::new);

    // Extended Crafting Tables (Tier 1: 4x4 up to Tier 10: 13x13)
    public static final RegistryObject<Block> TIER1_EXTENDED_CRAFTING_TABLE = registerBlock("tier1_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(1));
    public static final RegistryObject<Block> TIER2_EXTENDED_CRAFTING_TABLE = registerBlock("tier2_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(2));
    public static final RegistryObject<Block> TIER3_EXTENDED_CRAFTING_TABLE = registerBlock("tier3_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(3));
    public static final RegistryObject<Block> TIER4_EXTENDED_CRAFTING_TABLE = registerBlock("tier4_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(4));
    public static final RegistryObject<Block> TIER5_EXTENDED_CRAFTING_TABLE = registerBlock("tier5_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(5));
    public static final RegistryObject<Block> TIER6_EXTENDED_CRAFTING_TABLE = registerBlock("tier6_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(6));
    public static final RegistryObject<Block> TIER7_EXTENDED_CRAFTING_TABLE = registerBlock("tier7_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(7));
    public static final RegistryObject<Block> TIER8_EXTENDED_CRAFTING_TABLE = registerBlock("tier8_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(8));
    public static final RegistryObject<Block> TIER9_EXTENDED_CRAFTING_TABLE = registerBlock("tier9_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(9));
    public static final RegistryObject<Block> TIER10_EXTENDED_CRAFTING_TABLE = registerBlock("tier10_extended_crafting_table",
            () -> new com.exponentialmana.mod.block.ExtendedCraftingTableBlock(10));

    public static final java.util.List<RegistryObject<Block>> EXTENDED_CRAFTING_TABLES = java.util.List.of(
            TIER1_EXTENDED_CRAFTING_TABLE, TIER2_EXTENDED_CRAFTING_TABLE, TIER3_EXTENDED_CRAFTING_TABLE,
            TIER4_EXTENDED_CRAFTING_TABLE, TIER5_EXTENDED_CRAFTING_TABLE, TIER6_EXTENDED_CRAFTING_TABLE,
            TIER7_EXTENDED_CRAFTING_TABLE, TIER8_EXTENDED_CRAFTING_TABLE, TIER9_EXTENDED_CRAFTING_TABLE,
            TIER10_EXTENDED_CRAFTING_TABLE
    );

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
        registerBlockItem(name, registeredBlock);
        return registeredBlock;
    }

    private static <T extends Block> RegistryObject<T> registerCustomBlock(String name, Supplier<T> block, java.util.function.Function<T, BlockItem> itemFactory) {
        RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> itemFactory.apply(registeredBlock.get()));
        return registeredBlock;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
