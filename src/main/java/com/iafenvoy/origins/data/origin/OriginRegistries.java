package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.Origins;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.stream.Stream;

public final class OriginRegistries {
    public static final ResourceKey<Registry<Origin>> ORIGIN_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Origins.MOD_ID, "origin"));

    public static Stream<Holder.Reference<Origin>> streamAvailableOrigins(RegistryAccess access) {
        return access.lookupOrThrow(ORIGIN_KEY).listElements().filter(x -> !x.value().unchoosable());
    }
}
