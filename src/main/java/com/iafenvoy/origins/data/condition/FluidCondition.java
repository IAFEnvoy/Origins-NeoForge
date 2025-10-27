package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface FluidCondition {
    Codec<FluidCondition> CODEC = DefaultedCodec.registryDispatch(ConditionRegistries.FLUID_CONDITION, FluidCondition::codec, Function.identity(), () -> EmptyCondition.INSTANCE);

    static MapCodec<FluidCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends FluidCondition> codec();

    boolean test(@NotNull FluidState state);
}
