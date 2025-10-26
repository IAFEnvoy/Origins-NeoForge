package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.LivingEntity;

public class IgnoreWaterEvent extends EntityResultedEvent<LivingEntity> {
    public IgnoreWaterEvent(LivingEntity entity) {
        super(Result.DENY, entity);
    }
}
