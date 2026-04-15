package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.network.payload.PowerToggleC2SPayload;
import com.iafenvoy.origins.screen.ViewOriginScreen;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public final class OriginsKeyMappings {
    public static final String CATEGORY = "category.origins";
    public static final KeyMapping PRIMARY_ACTIVE = new KeyMapping(Constants.PRIMARY_ACTIVE_KEY, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping SECONDARY_ACTIVE = new KeyMapping(Constants.SECONDARY_ACTIVE_KEY, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping VIEW_ORIGIN = new KeyMapping("key.origins.view_origin", GLFW.GLFW_KEY_O, CATEGORY);
    public static final List<KeyMapping> ACTIVATE_KEYS = Util.make(new LinkedList<>(), list -> {
        list.add(PRIMARY_ACTIVE);
        list.add(SECONDARY_ACTIVE);
    });

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(PRIMARY_ACTIVE);
        event.register(SECONDARY_ACTIVE);
        event.register(VIEW_ORIGIN);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (VIEW_ORIGIN.consumeClick()) Minecraft.getInstance().setScreen(new ViewOriginScreen());
        for (KeyMapping key : ACTIVATE_KEYS)
            if (key.consumeClick()) PacketDistributor.sendToServer(new PowerToggleC2SPayload(key.getName()));
    }
}
