package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.IntervalPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class BurnPower extends IntervalPower {
    public static final MapCodec<BurnPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.fieldOf("interval").forGetter(BurnPower::getInterval),
            Codec.INT.fieldOf("burn_duration").forGetter(BurnPower::getBurnDuration)
    ).apply(i, BurnPower::new));
    private final int interval, burnDuration;

    public BurnPower(BaseSettings settings, int interval, int burnDuration) {
        super(settings);
        this.interval = interval;
        this.burnDuration = burnDuration;
    }

    public int getBurnDuration() {
        return this.burnDuration;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void intervalTick(@NotNull Entity entity) {
        entity.setRemainingFireTicks(this.burnDuration);
    }

    @Override
    public int getInterval() {
        return this.interval;
    }
}
