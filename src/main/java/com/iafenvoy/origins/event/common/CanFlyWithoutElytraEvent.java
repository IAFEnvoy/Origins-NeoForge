package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CanFlyWithoutElytraEvent extends EntityResultedEvent<LivingEntity> {
    public CanFlyWithoutElytraEvent(LivingEntity player) {
        super(Result.DENY, player);
    }
}
