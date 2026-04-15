package com.iafenvoy.origins.data.power;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

//FIXME::Map Codec
public abstract class IntervalPower extends Power {
    protected int remainTicks = 0;

    public IntervalPower(BaseSettings settings) {
        super(settings);
    }

    public IntervalPower(BaseSettings settings, int delay) {
        this(settings);
        this.remainTicks = delay;
    }

    @Override
    public void tick(@NotNull Entity entity) {
        if (this.remainTicks <= 0) {
            this.remainTicks = this.getInterval();
            this.intervalTick(entity);
        }
        this.remainTicks--;
    }

    public abstract int getInterval();

    public abstract void intervalTick(@NotNull Entity entity);
}
