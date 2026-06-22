package com.iafenvoy.origins.data.layer;

import com.iafenvoy.origins.Origins;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.stream.Stream;

public final class LayerRegistries {
    public static final ResourceKey<Registry<Layer>> LAYER_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Origins.MOD_ID, "layer"));

    @SuppressWarnings("unchecked")
    public static Stream<Holder<Layer>> streamAvailableLayers(RegistryAccess access) {
        return access.lookupOrThrow(LAYER_KEY).listElements().filter(x -> x.value().enabled()).map(Holder.class::cast);
    }

    public static Stream<Holder<Layer>> streamAutoChooseLayers(RegistryAccess access) {
        return streamAvailableLayers(access).filter(x -> x.value().autoChoose());
    }

    public static Stream<Holder<Layer>> streamRandomizableLayers(RegistryAccess access) {
        return streamAvailableLayers(access).filter(x -> x.value().allowRandom());
    }
}
