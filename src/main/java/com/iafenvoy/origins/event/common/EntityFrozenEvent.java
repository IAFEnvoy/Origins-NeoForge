package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.Entity;

public class EntityFrozenEvent extends EntityResultedEvent<Entity> {
    public EntityFrozenEvent(Entity entity) {
        super(Result.DENY, entity);
    }
}
