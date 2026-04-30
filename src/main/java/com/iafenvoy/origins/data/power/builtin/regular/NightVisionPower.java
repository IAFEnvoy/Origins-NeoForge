package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class NightVisionPower extends Power {
    public static final MapCodec<NightVisionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.floatRange(0, 1).optionalFieldOf("strength", 1F).forGetter(NightVisionPower::getStrength),
            EntityCondition.optionalCodec("condition").forGetter(NightVisionPower::getCondition)
    ).apply(i, NightVisionPower::new));
    private final float strength;
    private final EntityCondition condition;

    public NightVisionPower(BaseSettings settings, float strength, EntityCondition condition) {
        super(settings);
        this.strength = strength;
        this.condition = condition;
    }

    public float getStrength() {
        return this.strength;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
