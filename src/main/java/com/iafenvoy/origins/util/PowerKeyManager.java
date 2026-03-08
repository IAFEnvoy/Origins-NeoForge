package com.iafenvoy.origins.util;

import com.iafenvoy.origins.data.power.Power;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PowerKeyManager {

    private static final Map<ResourceLocation, String> KEY_CACHE = new HashMap<>();

    public static void clearCache() {
        KEY_CACHE.clear();
    }

    public static Optional<String> getKeyId(Power power, RegistryAccess access) {
        ResourceLocation powerId = power.getId(access);
        if (powerId == null) {
            return Optional.empty();
        }

        if (KEY_CACHE.containsKey(powerId)) {
            return Optional.ofNullable(KEY_CACHE.get(powerId));
        }

        // NeoForge power system does not have a unified Active type with key references,
        // so we return empty for now. Specific power implementations can override this.
        return Optional.empty();
    }
}
