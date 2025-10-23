package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiPredicate;

public interface ItemCondition extends BiPredicate<Level, ItemStack> {
    Codec<ItemCondition> CODEC = ConditionRegistries.ITEM_CONDITION.byNameCodec().dispatch("type", ItemCondition::type, ConditionType::codec);

    ConditionType<ItemCondition> type();

    @Override
    boolean test(Level level, ItemStack stack);
}
