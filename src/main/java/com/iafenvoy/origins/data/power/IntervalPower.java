package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class IntervalPower extends Power {
    private final Object2IntMap<UUID> remainTicks = new Object2IntOpenHashMap<>();
    private final int delay;

    protected IntervalPower(BaseSettings settings) {
        this(settings, 0);
    }

    protected IntervalPower(BaseSettings settings, int delay) {
        super(settings);
        this.delay = delay;
    }

    @Override
    public void revoke(@NotNull OriginDataHolder holder) {
        super.revoke(holder);
        this.remainTicks.removeInt(holder.getEntity().getUUID());
    }

    @Override
    public void tick(@NotNull OriginDataHolder holder) {
        super.tick(holder);
        if (!this.isActive(holder)) return;
        Entity entity = holder.getEntity();
        this.remainTicks.putIfAbsent(entity.getUUID(), this.delay);
        this.remainTicks.computeInt(entity.getUUID(), (uuid, tick) -> {
            if (tick <= 0) {
                tick = this.getInterval();
                this.intervalTick(holder.getEntity());
            }
            return --tick;
        });
    }

    public abstract int getInterval();

    public abstract void intervalTick(@NotNull Entity entity);
}
