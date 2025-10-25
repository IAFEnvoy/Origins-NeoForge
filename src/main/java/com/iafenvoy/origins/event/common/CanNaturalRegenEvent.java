package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.ResultedEvent;
import net.minecraft.world.entity.player.Player;

//Cancel this event to cancel natural regeneration
public class CanNaturalRegenEvent extends ResultedEvent {
    private final Player player;

    public CanNaturalRegenEvent(Player player) {
        super(Result.ALLOW);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
