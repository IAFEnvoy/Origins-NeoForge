package com.iafenvoy.origins.event;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;

@EventBusSubscriber
public final class CommonEvents {
    @SubscribeEvent
    public static void preventDamageWhenSelecting(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof Player player && !OriginDataHolder.get(player).hasAllOrigins())
            event.setInvulnerable(true);
    }
}
