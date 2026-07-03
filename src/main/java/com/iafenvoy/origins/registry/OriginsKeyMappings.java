package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.network.payload.PowerToggleC2SPayload;
import com.iafenvoy.origins.screen.ViewOriginScreen;
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
import java.util.Objects;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public enum OriginsKeyMappings {
    INSTANCE;

    public final String CATEGORY = "category.origins";
    public final KeyMapping PRIMARY_ACTIVE = new KeyMapping(Constants.PRIMARY_ACTIVE_KEY, GLFW.GLFW_KEY_G, this.CATEGORY);
    public final KeyMapping SECONDARY_ACTIVE = new KeyMapping(Constants.SECONDARY_ACTIVE_KEY, GLFW.GLFW_KEY_UNKNOWN, this.CATEGORY);
    public final KeyMapping VIEW_ORIGIN = new KeyMapping("key.origins.view_origin", GLFW.GLFW_KEY_O, this.CATEGORY);
    public final List<KeyMapping> ACTIVATE_KEYS = new LinkedList<>();

    public void registerKeyMappingsFromPowers(Set<PowerHolder> powerHolders) {
        this.ACTIVATE_KEYS.clear();

        for (PowerHolder powerHolder : powerHolders) {
            Power power = powerHolder.power();
            if (power instanceof Toggleable toggleable) {
                List<KeyMapping> keys = KeyMapping.ALL.values().stream().filter(keyMapping -> Objects.equals(toggleable.getKey().key(), keyMapping.getName())).toList();
                this.ACTIVATE_KEYS.addAll(keys);
            }
        }
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(INSTANCE.PRIMARY_ACTIVE);
        event.register(INSTANCE.SECONDARY_ACTIVE);
        event.register(INSTANCE.VIEW_ORIGIN);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (INSTANCE.VIEW_ORIGIN.consumeClick()) Minecraft.getInstance().setScreen(new ViewOriginScreen());
        for (KeyMapping key : INSTANCE.ACTIVATE_KEYS)
            if (key.consumeClick()) PacketDistributor.sendToServer(new PowerToggleC2SPayload(key.getName()));
    }
}
