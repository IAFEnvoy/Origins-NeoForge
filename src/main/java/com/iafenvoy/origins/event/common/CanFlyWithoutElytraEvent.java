package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.player.Player;

public class CanFlyWithoutElytraEvent extends EntityResultedEvent<Player> {
    public CanFlyWithoutElytraEvent(Player player) {
        super(Result.DENY, player);
    }
}
