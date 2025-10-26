package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.Entity;

public class EntityFireImmuneEvent extends EntityResultedEvent<Entity> {
    public EntityFireImmuneEvent(Entity entity) {
        super(Result.DENY, entity);
    }
}
