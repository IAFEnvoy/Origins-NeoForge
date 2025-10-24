package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.Origins;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber
public final class OriginRegistries {
    public static final ResourceKey<Registry<Origin>> ORIGIN_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "origin"));
    public static final Codec<Holder<Origin>> ORIGIN_CODEC = RegistryFixedCodec.create(ORIGIN_KEY);

    @SubscribeEvent
    public static void newDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ORIGIN_KEY, Origin.CODEC);
    }
}
