package com.iafenvoy.origins.event.client;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.LivingEntity;

public class ClientShouldGlowingEvent extends EntityResultedEvent<LivingEntity> {
    public ClientShouldGlowingEvent(LivingEntity entity) {
        super(Result.DENY, entity);
    }
}
