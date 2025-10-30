package com.iafenvoy.origins.data.condition.builtin.fluid;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public enum StillCondition implements FluidCondition {
    INSTANCE;
    public static final MapCodec<StillCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        return state.isSource();
    }
}
