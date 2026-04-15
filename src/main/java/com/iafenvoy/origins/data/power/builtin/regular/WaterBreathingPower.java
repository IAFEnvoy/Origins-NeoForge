package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class WaterBreathingPower extends Power {
    public static final MapCodec<WaterBreathingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("condition").forGetter(waterBreathingPower -> waterBreathingPower.getCondition())
    ).apply(i, WaterBreathingPower::new));
    private final EntityCondition condition;

    public WaterBreathingPower(BaseSettings settings, EntityCondition condition) {
        super(settings);
        this.condition = condition;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
