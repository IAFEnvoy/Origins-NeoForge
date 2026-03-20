package com.iafenvoy.origins.data.badge;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;

public final class BadgeManager {
    private static final Multimap<ResourceLocation, Badge> BADGE_BY_ID = HashMultimap.create();

    public static Collection<Badge> get(ResourceLocation id) {
        return BADGE_BY_ID.get(id);
    }

    public static boolean has(ResourceLocation id) {
        return !BADGE_BY_ID.get(id).isEmpty();
    }

    public static void put(ResourceLocation powerId, Badge badge) {
        BADGE_BY_ID.put(powerId, badge);
    }

    public static void clear() {
        BADGE_BY_ID.clear();
    }

    public static void putAll(Map<ResourceLocation, Collection<Badge>> badges) {
        badges.forEach(BADGE_BY_ID::putAll);
    }
}
