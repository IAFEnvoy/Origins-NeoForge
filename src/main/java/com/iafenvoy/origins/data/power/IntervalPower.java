package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class IntervalPower extends Power {
    private int remainTicks = 0;

    public IntervalPower(BaseSettings settings) {
        super(settings);
    }

    public IntervalPower(BaseSettings settings, int delay) {
        this(settings);
        this.remainTicks = delay;
    }

    @Override
    public void tick(@NotNull OriginDataHolder entity) {
        if (this.remainTicks <= 0) {
            this.remainTicks = this.getInterval();
            this.intervalTick(entity.getEntity());
        }
        this.remainTicks--;
    }

    public abstract int getInterval();

    public abstract void intervalTick(@NotNull Entity entity);
}
