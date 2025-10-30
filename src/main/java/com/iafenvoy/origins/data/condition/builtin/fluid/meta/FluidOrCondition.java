package com.iafenvoy.origins.data.condition.builtin.fluid.meta;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FluidOrCondition(List<FluidCondition> conditions) implements FluidCondition {
    public static final MapCodec<FluidOrCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidCondition.CODEC.listOf().fieldOf("conditions").forGetter(FluidOrCondition::conditions)
    ).apply(i, FluidOrCondition::new));

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        return this.conditions.stream().anyMatch(x -> x.test(state));
    }
}
