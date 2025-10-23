package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Predicate;

public interface FluidCondition extends Predicate<FluidState> {
    Codec<FluidCondition> CODEC = ConditionRegistries.FLUID_CONDITION.byNameCodec().dispatch("type", FluidCondition::codec, x -> x);

    MapCodec<? extends FluidCondition> codec();

    @Override
    boolean test(FluidState state);
}
