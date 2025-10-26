package com.iafenvoy.origins.data.power;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

public enum EmptyPower implements Power {
    INSTANCE;
    public static final MapCodec<EmptyPower> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
