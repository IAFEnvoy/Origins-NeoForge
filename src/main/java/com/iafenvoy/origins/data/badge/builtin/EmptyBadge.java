package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EmptyBadge implements Badge {
    public static final MapCodec<EmptyBadge> CODEC = MapCodec.unit(EmptyBadge::new);

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation sprite() {
        return ResourceLocation.withDefaultNamespace("missingno");
    }
}
