package com.iafenvoy.origins.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class RLHelper {
    public static final ResourceLocation EMPTY = ResourceLocation.withDefaultNamespace("");

    public static ResourceLocation id(Holder<?> holder) {
        return holder.unwrapKey().map(ResourceKey::location).orElse(EMPTY);
    }

    public static String string(Holder<?> holder) {
        return id(holder).toString();
    }
}
