package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.ResultedEvent;
import net.minecraft.world.entity.player.Player;

public class CanFlyWithoutElytraEvent extends ResultedEvent {
    private final Player player;

    public CanFlyWithoutElytraEvent(Player player) {
        super(Result.DENY);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
