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
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public final class BadgeRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends Badge>>> BADGE_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "badge_type"));
    public static final Registry<MapCodec<? extends Badge>> BADGE_TYPE = new MappedRegistry<>(BADGE_TYPE_KEY, Lifecycle.stable());

    public static final ResourceKey<Registry<Badge>> BADGE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "badge"));

    @SubscribeEvent
    public static void newRegistries(NewRegistryEvent event) {
        event.register(BADGE_TYPE);
    }

    @SubscribeEvent
    public static void newDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BADGE_KEY, Badge.CODEC, Badge.CODEC);
    }
}
