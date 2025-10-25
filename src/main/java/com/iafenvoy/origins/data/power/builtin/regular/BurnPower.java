package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.IntervalPower;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class BurnPower extends IntervalPower {
    public static final MapCodec<BurnPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("interval").forGetter(BurnPower::getInterval),
            Codec.INT.fieldOf("burn_duration").forGetter(BurnPower::getBurnDuration)
    ).apply(i, BurnPower::new));
    private final int interval, burnDuration;

    public BurnPower(int interval, int burnDuration) {
        this.interval = interval;
        this.burnDuration = burnDuration;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void intervalTick(@NotNull Entity entity) {
        entity.setRemainingFireTicks(this.burnDuration);
    }

    public int getInterval() {
        return this.interval;
    }

    public int getBurnDuration() {
        return this.burnDuration;
    }
}
