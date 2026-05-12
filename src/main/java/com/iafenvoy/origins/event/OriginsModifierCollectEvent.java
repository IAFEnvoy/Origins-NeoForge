package com.iafenvoy.origins.event;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.math.Modifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.EntityEvent;

import java.util.LinkedList;
import java.util.List;

public class OriginsModifierCollectEvent extends EntityEvent {
    private final Class<? extends Power> powerClass;
    private final double baseValue;
    private final List<Modifier> modifier;

    public OriginsModifierCollectEvent(Entity entity, Class<? extends Power> powerClass, double baseValue, List<Modifier> modifier) {
        super(entity);
        this.powerClass = powerClass;
        this.baseValue = baseValue;
        this.modifier = new LinkedList<>(modifier);
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
