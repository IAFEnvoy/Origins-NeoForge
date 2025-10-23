package com.iafenvoy.origins.data.badge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record BadgeType(MapCodec<? extends Badge> codec) {
}
