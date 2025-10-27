package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.IntervalPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ExhaustPower extends IntervalPower {
    public static final MapCodec<ExhaustPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("interval", 20).forGetter(ExhaustPower::getInterval),
            Codec.FLOAT.fieldOf("exhaustion").forGetter(ExhaustPower::getExhaustion),
            EntityCondition.optionalCodec("condition").forGetter(ExhaustPower::getCondition)
    ).apply(i, ExhaustPower::new));
    private final int interval;
    private final float exhaustion;
    private final EntityCondition condition;

    public ExhaustPower(int interval, float exhaustion, EntityCondition condition) {
        this.interval = interval;
        this.exhaustion = exhaustion;
        this.condition = condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void intervalTick(@NotNull Entity entity) {
        if (entity instanceof Player player && this.condition.test(entity))
            player.causeFoodExhaustion(this.exhaustion);
    }

    @Override
    public int getInterval() {
        return this.interval;
    }

    public float getExhaustion() {
        return this.exhaustion;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }
}
