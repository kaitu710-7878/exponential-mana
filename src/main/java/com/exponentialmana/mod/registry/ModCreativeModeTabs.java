package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExponentialMana.MOD_ID);

    public static final RegistryObject<CreativeModeTab> EXPONENTIAL_MANA_TAB =
            CREATIVE_MODE_TABS.register("exponential_mana_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.TIER0_MANA.get()))
                    .title(Component.translatable("itemGroup.exponential_mana"))
                    .displayItems((parameters, output) -> {
                        // Mana Items
                        output.accept(ModItems.TIER0_MANA.get());
                        output.accept(ModItems.TIER1_MANA.get());
                        output.accept(ModItems.TIER2_MANA.get());
                        output.accept(ModItems.TIER3_MANA.get());
                        output.accept(ModItems.TIER4_MANA.get());
                        output.accept(ModItems.TIER5_MANA.get());
                        output.accept(ModItems.TIER6_MANA.get());
                        output.accept(ModItems.TIER7_MANA.get());
                        output.accept(ModItems.TIER8_MANA.get());
                        output.accept(ModItems.TIER9_MANA.get());
                        output.accept(ModItems.TIER10_MANA.get());

                        // Cores
                        output.accept(ModItems.TIER1_CORE.get());
                        output.accept(ModItems.TIER2_CORE.get());
                        output.accept(ModItems.TIER3_CORE.get());
                        output.accept(ModItems.TIER4_CORE.get());
                        output.accept(ModItems.TIER5_CORE.get());
                        output.accept(ModItems.TIER6_CORE.get());
                        output.accept(ModItems.TIER7_CORE.get());
                        output.accept(ModItems.TIER8_CORE.get());
                        output.accept(ModItems.TIER9_CORE.get());
                        output.accept(ModItems.TIER10_CORE.get());

                        // Catalysts & Residue
                        output.accept(ModItems.FAIRY_RESIDUE.get());
                        output.accept(ModItems.TIER1_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER2_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER3_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER4_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER5_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER6_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER7_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER8_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER9_FAIRY_CATALYST.get());
                        output.accept(ModItems.TIER10_FAIRY_CATALYST.get());
                        output.accept(ModItems.PURE_FAIRY_CATALYST.get());

                        // Shards & Mini Tables
                        output.accept(ModItems.TIER1_MANA_ORE_SHARD.get());
                        output.accept(ModItems.TIER1_FAIRY_CATALYST_SHARD.get());
                        output.accept(ModItems.TIER1_MINI_CRAFTING_TABLE.get());

                        output.accept(ModItems.TIER3_MANA_ORE_SHARD.get());
                        output.accept(ModItems.TIER3_FAIRY_CATALYST_SHARD.get());
                        output.accept(ModItems.TIER3_MINI_CRAFTING_TABLE.get());

                        output.accept(ModItems.TIER5_MANA_ORE_SHARD.get());
                        output.accept(ModItems.TIER5_FAIRY_CATALYST_SHARD.get());
                        output.accept(ModItems.TIER5_MINI_CRAFTING_TABLE.get());

                        output.accept(ModItems.TIER7_MANA_ORE_SHARD.get());
                        output.accept(ModItems.TIER7_FAIRY_CATALYST_SHARD.get());
                        output.accept(ModItems.TIER7_MINI_CRAFTING_TABLE.get());

                        output.accept(ModItems.TIER9_MANA_ORE_SHARD.get());
                        output.accept(ModItems.TIER9_FAIRY_CATALYST_SHARD.get());
                        output.accept(ModItems.TIER9_MINI_CRAFTING_TABLE.get());

                        // Endgame Items
                        output.accept(ModItems.PURE_MANA.get());
                        output.accept(ModItems.ORIGIN_ALLOY.get());
                        output.accept(ModItems.COMPRESSED_ORIGIN_ALLOY.get());
                        output.accept(ModItems.MANA_KNOWLEDGE.get());
                        output.accept(ModItems.MANA_WISDOM.get());
                        output.accept(ModItems.UNCOMPRESSED_STAR_MANA.get());
                        output.accept(ModItems.STAR_MANA.get());
                        output.accept(ModItems.UNCOMPRESSED_QUANTUM_MANA.get());
                        output.accept(ModItems.QUANTUM_MANA.get());
                        output.accept(ModItems.UNCOMPRESSED_ABYSSAL_MANA.get());
                        output.accept(ModItems.ABYSSAL_MANA.get());
                        output.accept(ModItems.UNCOMPRESSED_MARINE_MANA.get());
                        output.accept(ModItems.MARINE_MANA.get());
                        output.accept(ModItems.MANA_ROOT.get());
                        output.accept(ModItems.INFINITE_MANA.get());
                        output.accept(ModItems.BATCH_COMPRESSED_MANA.get());

                        // Origin Equipment
                        output.accept(ModItems.ORIGIN_HELMET.get());
                        output.accept(ModItems.ORIGIN_CHESTPLATE.get());
                        output.accept(ModItems.ORIGIN_LEGGINGS.get());
                        output.accept(ModItems.ORIGIN_BOOTS.get());
                        output.accept(ModItems.ORIGIN_SWORD.get());

                        // Mana Materials & Compressed Materials
                        output.accept(ModItems.TIER1_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER2_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER3_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER4_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER5_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER6_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER7_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER8_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER9_MANA_MATERIAL.get());
                        output.accept(ModItems.TIER10_MANA_MATERIAL.get());

                        output.accept(ModItems.COMPRESSED_TIER1_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER2_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER3_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER4_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER5_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER6_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER7_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER8_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER9_MANA_MATERIAL.get());
                        output.accept(ModItems.COMPRESSED_TIER10_MANA_MATERIAL.get());

                        output.accept(ModItems.COMPRESSED_LAPIS.get());
                        output.accept(ModItems.COMPRESSED_AMETHYST.get());
                        output.accept(ModItems.COMPRESSED_QUARTZ.get());

                        // Mana Symbols
                        output.accept(ModItems.MANA_SYMBOL_TIER0.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER1.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER2.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER3.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER4.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER5.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER6.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER7.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER8.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER9.get());
                        output.accept(ModItems.MANA_SYMBOL_TIER10.get());

                        // Blocks - Active Ores
                        output.accept(ModBlocks.TIER0_MANA_ORE.get());
                        output.accept(ModBlocks.TIER1_MANA_ORE.get());
                        output.accept(ModBlocks.TIER2_MANA_ORE.get());
                        output.accept(ModBlocks.TIER3_MANA_ORE.get());
                        output.accept(ModBlocks.TIER4_MANA_ORE.get());
                        output.accept(ModBlocks.TIER5_MANA_ORE.get());
                        output.accept(ModBlocks.TIER6_MANA_ORE.get());
                        output.accept(ModBlocks.TIER7_MANA_ORE.get());
                        output.accept(ModBlocks.TIER8_MANA_ORE.get());
                        output.accept(ModBlocks.TIER9_MANA_ORE.get());
                        output.accept(ModBlocks.TIER10_MANA_ORE.get());
                        output.accept(ModBlocks.PURE_MANA_ORE.get());

                        // Blocks - Inactive Ores
                        output.accept(ModBlocks.INACTIVE_TIER0_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER1_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER2_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER3_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER4_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER5_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER6_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER7_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER8_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER9_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_TIER10_MANA_ORE.get());
                        output.accept(ModBlocks.INACTIVE_PURE_MANA_ORE.get());

                        // Blocks - Fairy & Automation
                        output.accept(ModBlocks.FAIRY_BLOCK.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_1.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_2.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_3.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_4.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_5.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_6.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_7.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_8.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_9.get());
                        output.accept(ModBlocks.COMPRESSED_FAIRY_BLOCK_10.get());
                        output.accept(ModBlocks.FAIRY_ATTRACTOR.get());

                        // Blocks - Batch Processing & Thawing
                        output.accept(ModBlocks.BATCH_ORE_BREAKER.get());
                        output.accept(ModBlocks.THAWER.get());

                        // Blocks - Extended Crafting Tables (Tier 1 to 10)
                        output.accept(ModBlocks.TIER1_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER2_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER3_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER4_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER5_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER6_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER7_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER8_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER9_EXTENDED_CRAFTING_TABLE.get());
                        output.accept(ModBlocks.TIER10_EXTENDED_CRAFTING_TABLE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
