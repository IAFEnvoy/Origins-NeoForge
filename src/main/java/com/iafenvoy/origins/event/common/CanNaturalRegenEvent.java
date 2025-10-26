package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.player.Player;

public class CanNaturalRegenEvent extends EntityResultedEvent<Player> {
    public CanNaturalRegenEvent(Player player) {
        super(Result.ALLOW, player);
    }
}
