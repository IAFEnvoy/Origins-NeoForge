package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ConduitPowerOnLandPower extends Power {
    public static final MapCodec<ConduitPowerOnLandPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("condition").forGetter(ConduitPowerOnLandPower::getCondition)
    ).apply(i, ConduitPowerOnLandPower::new));
    private final EntityCondition condition;

    public ConduitPowerOnLandPower(BaseSettings settings, EntityCondition condition) {
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
