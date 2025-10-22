package com.iafenvoy.origins.data.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record PowerType(MapCodec<? extends Power> codec) {
    public static final Codec<Power> CODEC = PowerRegistries.POWER_TYPE.byNameCodec().dispatch("type", Power::type, PowerType::codec);
}
