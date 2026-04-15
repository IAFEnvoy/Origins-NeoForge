package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class LaunchPower extends Power {
    public static final MapCodec<LaunchPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(LaunchPower::getCooldown),
            Codec.FLOAT.optionalFieldOf("speed", 1F).forGetter(LaunchPower::getSpeed),
            EntityCondition.optionalCodec("condition").forGetter(LaunchPower::getCondition)
    ).apply(i, LaunchPower::new));
    private final int cooldown;
    private final float speed;
    private final EntityCondition condition;

    public LaunchPower(BaseSettings settings, int cooldown, float speed, EntityCondition condition) {
        super(settings);
        this.cooldown = cooldown;
        this.speed = speed;
        this.condition = condition;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public float getSpeed() {
        return this.speed;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
