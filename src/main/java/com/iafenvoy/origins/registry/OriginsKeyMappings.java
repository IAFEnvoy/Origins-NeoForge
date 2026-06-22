package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.network.payload.PowerToggleC2SPayload;
import com.iafenvoy.origins.screen.ViewOriginScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@EventBusSubscriber(Dist.CLIENT)
public enum OriginsKeyMappings {
    INSTANCE;
    
    public final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath("origins", "main"));
    public final KeyMapping PRIMARY_ACTIVE = new KeyMapping(Constants.PRIMARY_ACTIVE_KEY, GLFW.GLFW_KEY_G, CATEGORY);
    public final KeyMapping SECONDARY_ACTIVE = new KeyMapping(Constants.SECONDARY_ACTIVE_KEY, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public final KeyMapping VIEW_ORIGIN = new KeyMapping("key.origins.view_origin", GLFW.GLFW_KEY_O, CATEGORY);
    public final List<KeyMapping> ACTIVATE_KEYS = new LinkedList<>();

    public void registerKeyMappingsFromPowers(Set<PowerHolder> powerHolders) {
        ACTIVATE_KEYS.clear();

        for (PowerHolder powerHolder : powerHolders) {
            Power power = powerHolder.power();
            if (power instanceof Toggleable toggleable) {
                List<KeyMapping> keys = KeyMapping.ALL.values().stream().filter(keyMapping -> Objects.equals(toggleable.getKey().key(), keyMapping.getName())).toList();
                ACTIVATE_KEYS.addAll(keys);
            }
        }
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(INSTANCE.CATEGORY);
        event.register(INSTANCE.PRIMARY_ACTIVE);
        event.register(INSTANCE.SECONDARY_ACTIVE);
        event.register(INSTANCE.VIEW_ORIGIN);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (INSTANCE.VIEW_ORIGIN.consumeClick()) Minecraft.getInstance().setScreen(new ViewOriginScreen());
        for (KeyMapping key : INSTANCE.ACTIVATE_KEYS)
            if (key.consumeClick()) ClientPacketDistributor.sendToServer(new PowerToggleC2SPayload(key.getName()));
    }
}
