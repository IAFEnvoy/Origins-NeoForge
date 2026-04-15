package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.LivingEntity;

public class CanClimbEvent extends EntityResultedEvent<LivingEntity> {
    public CanClimbEvent(LivingEntity entity) {
        super(Result.DENY, entity);
    }
}
