package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
import com.exponentialmana.mod.block.FairyBlock;
import com.exponentialmana.mod.block.entity.FairyAttractorBlockEntity;
import com.exponentialmana.mod.block.entity.FairyBlockEntity;
import com.exponentialmana.mod.block.entity.InactiveManaOreBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExponentialMana.MOD_ID);

    public static final RegistryObject<BlockEntityType<InactiveManaOreBlockEntity>> INACTIVE_MANA_ORE_BE =
            BLOCK_ENTITIES.register("inactive_mana_ore", () ->
                    BlockEntityType.Builder.of((pos, state) ->
                            new InactiveManaOreBlockEntity(pos, state, null),
                            ModBlocks.INACTIVE_TIER0_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER1_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER2_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER3_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER4_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER5_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER6_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER7_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER8_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER9_MANA_ORE.get(),
                            ModBlocks.INACTIVE_TIER10_MANA_ORE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<FairyBlockEntity>> FAIRY_BLOCK_BE =
            BLOCK_ENTITIES.register("fairy_block", () ->
                    BlockEntityType.Builder.of((pos, state) -> {
                        int tier = 0;
                        if (state.getBlock() instanceof FairyBlock fb) {
                            tier = fb.getCompressionTier();
                        }
                        return new FairyBlockEntity(pos, state, tier);
                    },
                    ModBlocks.FAIRY_BLOCK.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_1.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_2.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_3.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_4.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_5.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_6.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_7.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_8.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_9.get(),
                    ModBlocks.COMPRESSED_FAIRY_BLOCK_10.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<FairyAttractorBlockEntity>> FAIRY_ATTRACTOR_BE =
            BLOCK_ENTITIES.register("fairy_attractor", () ->
                    BlockEntityType.Builder.of(FairyAttractorBlockEntity::new,
                            ModBlocks.FAIRY_ATTRACTOR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<com.exponentialmana.mod.block.entity.BatchOreBreakerBlockEntity>> BATCH_ORE_BREAKER_BE =
            BLOCK_ENTITIES.register("batch_ore_breaker", () ->
                    BlockEntityType.Builder.of(com.exponentialmana.mod.block.entity.BatchOreBreakerBlockEntity::new,
                            ModBlocks.BATCH_ORE_BREAKER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<com.exponentialmana.mod.block.entity.ThawerBlockEntity>> THAWER_BE =
            BLOCK_ENTITIES.register("thawer", () ->
                    BlockEntityType.Builder.of(com.exponentialmana.mod.block.entity.ThawerBlockEntity::new,
                            ModBlocks.THAWER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<com.exponentialmana.mod.block.entity.ExtendedCraftingTableBlockEntity>> EXTENDED_CRAFTING_TABLE_BE =
            BLOCK_ENTITIES.register("extended_crafting_table", () ->
                    BlockEntityType.Builder.of((pos, state) -> {
                        int tier = 1;
                        if (state.getBlock() instanceof com.exponentialmana.mod.block.ExtendedCraftingTableBlock tb) {
                            tier = tb.getTier();
                        }
                        return new com.exponentialmana.mod.block.entity.ExtendedCraftingTableBlockEntity(pos, state, tier);
                    },
                    ModBlocks.TIER1_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER2_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER3_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER4_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER5_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER6_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER7_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER8_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER9_EXTENDED_CRAFTING_TABLE.get(),
                    ModBlocks.TIER10_EXTENDED_CRAFTING_TABLE.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
