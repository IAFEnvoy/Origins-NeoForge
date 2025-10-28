package com.iafenvoy.origins.data.layer;

import com.iafenvoy.origins.Origins;
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
public final class LayerRegistries {
    public static final ResourceKey<Registry<Layer>> LAYER_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "layer"));

    @SubscribeEvent
    public static void newDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LAYER_KEY, Layer.DIRECT_CODEC, Layer.DIRECT_CODEC);
    }

    @SuppressWarnings("unchecked")
    public static Stream<Holder<Layer>> streamAvailableLayers(RegistryAccess access) {
        return access.registryOrThrow(LAYER_KEY).holders().filter(x -> x.value().enabled()).map(Holder.class::cast);
    }

    public static Stream<Holder<Layer>> streamAutoChooseLayers(RegistryAccess access) {
        return streamAvailableLayers(access).filter(x -> x.value().autoChoose());
    }
}
