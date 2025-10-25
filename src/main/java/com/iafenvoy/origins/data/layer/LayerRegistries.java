package com.iafenvoy.origins.data.layer;

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
public final class LayerRegistries {
    public static final ResourceKey<Registry<Layer>> LAYER_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "layer"));
    public static final Codec<Holder<Layer>> LAYER_CODEC = RegistryFixedCodec.create(LAYER_KEY);

    @SubscribeEvent
    public static void newDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LAYER_KEY, Layer.CODEC);
    }
}
