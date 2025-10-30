package com.iafenvoy.origins.data.condition.builtin.fluid.meta;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public record FluidNotCondition(FluidCondition condition) implements FluidCondition {
    public static final MapCodec<FluidNotCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidCondition.CODEC.fieldOf("condition").forGetter(FluidNotCondition::new)
    ).apply(i, FluidNotCondition::new));

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        return !this.condition.test(state);
    }
}
