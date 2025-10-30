package com.iafenvoy.origins.data.condition.builtin.fluid.meta;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public record FluidChanceCondition(double chance) implements FluidCondition {
    public static final MapCodec<FluidChanceCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FluidChanceCondition::chance)
    ).apply(i, FluidChanceCondition::new));

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        return Math.random() < this.chance;
    }
}
