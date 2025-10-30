package com.iafenvoy.origins.data.condition.builtin.item.meta;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ItemOrCondition(List<ItemCondition> conditions) implements ItemCondition {
    public static final MapCodec<ItemOrCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.CODEC.listOf().fieldOf("conditions").forGetter(ItemOrCondition::conditions)
    ).apply(i, ItemOrCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return this.conditions.stream().anyMatch(x -> x.test(level, stack));
    }
}
