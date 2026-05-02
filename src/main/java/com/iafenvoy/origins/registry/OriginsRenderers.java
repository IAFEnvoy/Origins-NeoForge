package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.data.badge.BuiltinBadges;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.render.BadgeTooltipManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public final class OriginsRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OriginsEntities.ENDERIAN_PEARL.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void registerBadgeTooltips(FMLClientSetupEvent event) {
        BadgeTooltipManager.register(BuiltinBadges.KEYBIND.get(), (badge, power, font, widthLimit, delta) -> {
            if (power instanceof Toggleable toggleable) {
                KeyMapping key = KeyMapping.ALL.get(toggleable.getKey().key());
                return List.of(ClientTooltipComponent.create(Component.translatable(badge.text(), Component.literal("[").append(key.getKey().getDisplayName()).append("]")).getVisualOrderText()));
            }
            return List.of();
        });
        BadgeTooltipManager.register(BuiltinBadges.TOOLTIP.get(), (badge, power, font, widthLimit, delta) -> List.of(ClientTooltipComponent.create(badge.text().getVisualOrderText())));
    }
}
