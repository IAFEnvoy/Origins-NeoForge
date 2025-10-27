package com.iafenvoy.origins.data.badge;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public final class BadgeManager {
    private static final Multimap<ResourceLocation, Badge> BADGE_BY_ID = HashMultimap.create();

    public static Collection<Badge> get(ResourceLocation id) {
        return BADGE_BY_ID.get(id);
    }

    public static boolean has(ResourceLocation id) {
        return !BADGE_BY_ID.get(id).isEmpty();
    }
}
