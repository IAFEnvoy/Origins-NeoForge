package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.stream.Stream;

@EventBusSubscriber
public final class OriginRegistries {
    public static final ResourceKey<Registry<Origin>> ORIGIN_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "origin"));

    @SubscribeEvent
    public static void newDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ORIGIN_KEY, Origin.DIRECT_CODEC, Origin.DIRECT_CODEC);
    }

    @SuppressWarnings("unchecked")
    public static Stream<Holder<Layer>> streamAvailableOrigins(RegistryAccess access) {
        return access.registryOrThrow(ORIGIN_KEY).holders().filter(x -> x.value().choosable()).map(Holder.class::cast);
    }
}
