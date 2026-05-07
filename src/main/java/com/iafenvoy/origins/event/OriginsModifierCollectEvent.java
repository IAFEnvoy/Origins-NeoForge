package com.iafenvoy.origins.event;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.math.Modifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

import java.util.List;

public class OriginsModifierCollectEvent extends Event {
    private final Entity entity;
    private final Class<? extends Power> powerClass;
    private final double baseValue;
    private final List<Modifier> modifier;

    public OriginsModifierCollectEvent(Entity entity, Class<? extends Power> powerClass, double baseValue, List<Modifier> modifier) {
        this.entity = entity;
        this.powerClass = powerClass;
        this.baseValue = baseValue;
        this.modifier = modifier;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Class<? extends Power> getPowerClass() {
        return this.powerClass;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public List<Modifier> getModifier() {
        return this.modifier;
    }
}
