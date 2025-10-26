package com.iafenvoy.origins.event;

import net.minecraft.world.entity.Entity;

public class EntityResultedEvent<T extends Entity> extends ResultedEvent {
    protected final T entity;

    public EntityResultedEvent(Result defaultResult, T entity) {
        super(defaultResult);
        this.entity = entity;
    }

    public T getEntity() {
        return this.entity;
    }
}
