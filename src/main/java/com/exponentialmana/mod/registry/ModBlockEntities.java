package com.exponentialmana.mod.registry;

import com.exponentialmana.mod.ExponentialMana;
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
                    BlockEntityType.Builder.of((pos, state) -> {
                        // Will be dynamically resolved based on state in InactiveManaOreBlock
                        return new InactiveManaOreBlockEntity(pos, state, () -> null);
                    },
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

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
