package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class NightVisionPower extends Power {
    public static final MapCodec<NightVisionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.floatRange(0, 1).optionalFieldOf("strength", 1F).forGetter(NightVisionPower::getStrength)
    ).apply(i, NightVisionPower::new));
    private final float strength;

    public NightVisionPower(BaseSettings settings, float strength) {
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
