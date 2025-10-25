package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.ResultedEvent;
import net.minecraft.world.entity.Entity;

public class EntityFrozenEvent extends ResultedEvent {
    private final Entity entity;

    public EntityFrozenEvent(Entity entity) {
        super(Result.DENY);
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
