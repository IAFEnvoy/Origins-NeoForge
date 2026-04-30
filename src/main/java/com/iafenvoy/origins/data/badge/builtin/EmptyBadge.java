package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public enum EmptyBadge implements Badge {
    INSTANCE;
    public static final MapCodec<EmptyBadge> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation spriteId() {
        return ResourceLocation.withDefaultNamespace("missingno");
    }
}
