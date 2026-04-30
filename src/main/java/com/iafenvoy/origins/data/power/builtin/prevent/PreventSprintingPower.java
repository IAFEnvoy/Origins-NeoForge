package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventSprintingPower extends Power {
    public static final MapCodec<PreventSprintingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings)
    ).apply(i, PreventSprintingPower::new));

    public PreventSprintingPower(BaseSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
