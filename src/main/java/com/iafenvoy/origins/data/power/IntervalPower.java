package com.iafenvoy.origins.data.power;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class IntervalPower implements Power {
    protected int remainTicks = 0;

    public IntervalPower() {
    }

    public IntervalPower(int delay) {
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
