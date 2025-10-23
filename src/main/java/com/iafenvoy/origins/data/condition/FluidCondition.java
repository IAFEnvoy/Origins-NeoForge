package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface FluidCondition extends Predicate<FluidState> {
    Codec<FluidCondition> CODEC = ConditionRegistries.FLUID_CONDITION.byNameCodec().dispatch("type", FluidCondition::codec, x -> x);

    @NotNull
    MapCodec<? extends FluidCondition> codec();

    @Override
    boolean test(@NotNull FluidState state);
}
