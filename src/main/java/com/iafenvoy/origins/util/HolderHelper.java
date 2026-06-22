package com.iafenvoy.origins.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public final class HolderHelper {
    public static final Identifier EMPTY = Identifier.withDefaultNamespace("");

    public static Identifier id(Holder<?> holder) {
        return holder.unwrapKey().map(ResourceKey::identifier).orElse(EMPTY);
    }

    public static String string(Holder<?> holder) {
        return id(holder).toString();
    }
}
