package com.iafenvoy.origins.registry;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(Dist.CLIENT)
public final class OriginsRenderers {
    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OriginsEntities.ENDERIAN_PEARL.get(), ThrownItemRenderer::new);
    }
}
