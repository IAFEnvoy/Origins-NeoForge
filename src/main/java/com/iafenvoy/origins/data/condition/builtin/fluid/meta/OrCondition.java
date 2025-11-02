package com.iafenvoy.origins.data.condition.builtin.fluid.meta;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record OrCondition(List<FluidCondition> conditions) implements FluidCondition {
    public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidCondition.CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::conditions)
    ).apply(i, OrCondition::new));

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        return this.conditions.stream().anyMatch(x -> x.test(state));
    }
}
