package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.screen.ViewOriginScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public final class OriginsKeyMappings {
    public static final String CATEGORY = "category.origins";
    public static final KeyMapping PRIMARY_ACTIVE = new KeyMapping("key.origins.primary_active", GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping SECONDARY_ACTIVE = new KeyMapping("key.origins.secondary_active", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping VIEW_ORIGIN = new KeyMapping("key.origins.view_origin", GLFW.GLFW_KEY_O, CATEGORY);

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(PRIMARY_ACTIVE);
        event.register(SECONDARY_ACTIVE);
        event.register(VIEW_ORIGIN);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (VIEW_ORIGIN.consumeClick()) Minecraft.getInstance().setScreen(new ViewOriginScreen());
    }
}
