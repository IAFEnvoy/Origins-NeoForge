package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface ItemCondition extends BiPredicate<Level, ItemStack> {
    Codec<ItemCondition> CODEC = ConditionRegistries.ITEM_CONDITION.byNameCodec().dispatch("type", ItemCondition::codec, x -> x);

    @NotNull
    MapCodec<? extends ItemCondition> codec();

    @Override
    boolean test(@NotNull Level level, @NotNull ItemStack stack);
}
