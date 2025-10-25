package com.iafenvoy.origins.event.client;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ClientGlowingColorEvent extends Event {
    private final Entity entity;
    private OptionalInt color = OptionalInt.empty();

    public ClientGlowingColorEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setColor(int color) {
        this.color = OptionalInt.of(color);
    }

    public void clearColor() {
        this.color = OptionalInt.empty();
    }

    public OptionalInt getColor() {
        return this.color;
    }
}
