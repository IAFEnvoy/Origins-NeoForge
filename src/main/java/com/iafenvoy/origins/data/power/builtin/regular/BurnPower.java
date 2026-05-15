package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class BurnPower extends Power {
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

    public int getInterval() {
        return this.interval;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public int tickInterval() {
        return this.interval;
    }

    @Override
    public void activeTick(@NotNull OriginDataHolder holder) {
        super.activeTick(holder);
        holder.getEntity().setRemainingFireTicks(this.burnDuration);
    }
}
