package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.Origins;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public final class OriginRegistries {
    public static final ResourceKey<Registry<Origin>> ORIGIN_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "origin"));

    public static Stream<Holder.Reference<Origin>> streamAvailableOrigins(RegistryAccess access) {
        return access.registryOrThrow(ORIGIN_KEY).holders().filter(x -> x.value().choosable());
    }
}
