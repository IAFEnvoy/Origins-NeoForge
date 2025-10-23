package com.iafenvoy.origins.data.badge;

import com.iafenvoy.origins.Origins;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public final class BadgeRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends Badge>>> BADGE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "badge"));
    public static final Registry<MapCodec<? extends Badge>> BADGE = new MappedRegistry<>(BADGE_KEY, Lifecycle.stable());

    @SubscribeEvent
    public static void newRegistries(NewRegistryEvent event) {
        event.register(BADGE);
    }
}
