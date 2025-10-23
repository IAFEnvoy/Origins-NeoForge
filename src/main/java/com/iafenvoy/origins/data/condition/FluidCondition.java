package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Predicate;

public interface FluidCondition extends Predicate<FluidState> {
    Codec<FluidCondition> CODEC = ConditionRegistries.FLUID_CONDITION.byNameCodec().dispatch("type", FluidCondition::type, ConditionType::codec);

    ConditionType<FluidCondition> type();

    @Override
    boolean test(FluidState state);
}
