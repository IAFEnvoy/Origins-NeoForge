package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.LivingEntity;

public class CanFlyWithoutElytraEvent extends EntityResultedEvent<LivingEntity> {
    public CanFlyWithoutElytraEvent(LivingEntity player) {
        super(Result.DENY, player);
    }
}
