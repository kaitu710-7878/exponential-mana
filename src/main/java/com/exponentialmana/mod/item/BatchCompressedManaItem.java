package com.exponentialmana.mod.item;

import com.exponentialmana.mod.util.CompactStackFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Item representing batch compressed mana holding astronomical counts in NBT.
 * Safely dropped when dismantling a Batch Ore Breaker to prevent server lag.
 */
public class BatchCompressedManaItem extends Item {
    public BatchCompressedManaItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    public static void addMana(ItemStack stack, int tier, long amount) {
        if (amount <= 0) return;
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag manaTag = tag.getCompound("ManaCounts");
        long current = manaTag.getLong("Tier" + tier);
        manaTag.putLong("Tier" + tier, current + amount);
        tag.put("ManaCounts", manaTag);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.exponential_mana.batch_compressed_mana.desc").withStyle(ChatFormatting.GRAY));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ManaCounts")) {
            CompoundTag manaTag = tag.getCompound("ManaCounts");
            boolean hasMana = false;
            for (int tier = 0; tier <= 10; tier++) {
                long count = manaTag.getLong("Tier" + tier);
                if (count > 0) {
                    hasMana = true;
                    tooltip.add(Component.literal(" - Tier " + tier + " Mana: ")
                            .append(Component.literal(CompactStackFormatter.formatCount(count) + " (" + String.format("%,d", count) + ")")
                                    .withStyle(ChatFormatting.AQUA)));
                }
            }
            if (!hasMana) {
                tooltip.add(Component.translatable("tooltip.exponential_mana.batch_compressed_mana.empty").withStyle(ChatFormatting.DARK_GRAY));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.exponential_mana.batch_compressed_mana.empty").withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
