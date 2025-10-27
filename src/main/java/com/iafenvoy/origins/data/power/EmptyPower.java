package com.iafenvoy.origins.data.power;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

public class EmptyPower implements Power {
    public static final MapCodec<EmptyPower> CODEC = MapCodec.unit(EmptyPower::new);

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
