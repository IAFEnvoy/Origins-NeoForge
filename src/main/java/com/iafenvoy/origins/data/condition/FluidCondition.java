package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public interface FluidCondition {
    Codec<FluidCondition> CODEC = ConditionRegistries.FLUID_CONDITION.byNameCodec().dispatch("type", FluidCondition::codec, x -> x);

    static MapCodec<FluidCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends FluidCondition> codec();

    boolean test(@NotNull FluidState state);
}
