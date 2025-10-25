package com.iafenvoy.origins.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

//Cancel this event to cancel natural regeneration
public class CheckNaturalRegenEvent extends Event implements ICancellableEvent {
    private final Player player;

    public CheckNaturalRegenEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
