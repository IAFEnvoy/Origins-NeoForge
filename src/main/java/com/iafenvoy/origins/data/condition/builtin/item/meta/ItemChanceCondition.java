package com.iafenvoy.origins.data.condition.builtin.item.meta;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ItemChanceCondition(double chance) implements ItemCondition {
    public static final MapCodec<ItemChanceCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(ItemChanceCondition::chance)
    ).apply(i, ItemChanceCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return Math.random() < this.chance;
    }
}
