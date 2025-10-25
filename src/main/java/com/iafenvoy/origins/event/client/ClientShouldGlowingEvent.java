package com.iafenvoy.origins.event.client;

import com.iafenvoy.origins.event.ResultedEvent;
import net.minecraft.world.entity.LivingEntity;

public class ClientShouldGlowingEvent extends ResultedEvent {
    private final LivingEntity entity;

    public ClientShouldGlowingEvent(LivingEntity entity) {
        super(Result.DENY);
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }
}
