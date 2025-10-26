package com.iafenvoy.origins.event.client;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;

import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ClientGlowingColorEvent extends EntityEvent {
    private OptionalInt color = OptionalInt.empty();

    public ClientGlowingColorEvent(Entity entity) {
        super(entity);
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
