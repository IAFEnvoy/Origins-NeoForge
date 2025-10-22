package com.iafenvoy.origins.data.badge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record BadgeType(MapCodec<? extends Badge> codec) {
    public static final Codec<Badge> CODEC = BadgeRegistries.BADGE.byNameCodec().dispatch("type", Badge::type, BadgeType::codec);
}
