package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class WaterVisionPower extends Power {
    public static final MapCodec<WaterVisionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.FLOAT.optionalFieldOf("strength", 1f).forGetter(WaterVisionPower::getStrength)
    ).apply(i, WaterVisionPower::new));
    private final float strength;

    public WaterVisionPower(BaseSettings settings, float strength) {
        super(settings);
        this.strength = strength;
    }

    public float getStrength() {
        return this.strength;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
