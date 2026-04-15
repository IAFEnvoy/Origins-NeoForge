package com.iafenvoy.origins.data.power.component;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public final class PowerComponentRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends PowerComponent>>> POWER_COMPONENT_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "power_component_type"));
    public static final DefaultedRegistry<MapCodec<? extends PowerComponent>> POWER_COMPONENT_TYPE = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, POWER_COMPONENT_TYPE_KEY, Lifecycle.stable(), false);

    @SubscribeEvent
    public static void newRegistries(NewRegistryEvent event) {
        event.register(POWER_COMPONENT_TYPE);
    }
}
