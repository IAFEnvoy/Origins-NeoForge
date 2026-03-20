package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

public class RecipePower implements Power {
    public static final MapCodec<RecipePower> CODEC = MapCodec.unit(RecipePower::new);

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
