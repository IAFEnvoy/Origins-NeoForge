package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ConduitPowerOnLandPower extends Power {
    public static final MapCodec<ConduitPowerOnLandPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings)
    ).apply(i, ConduitPowerOnLandPower::new));

    public ConduitPowerOnLandPower(BaseSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
