package com.iafenvoy.origins.event.handler;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.ApiStatus;

@EventBusSubscriber(Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void clearOriginHolderCache(ClientTickEvent.Post event) {
        OriginDataHolder.clearCache();
    }
}
