package com.iafenvoy.origins.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.Optional;
import java.util.OptionalInt;

public final class OptionalCodecs {
    public static MapCodec<OptionalInt> integer(String name) {
        return Codec.INT.optionalFieldOf(name).xmap(o -> o.map(OptionalInt::of).orElseGet(OptionalInt::empty), o -> o.isPresent() ? Optional.of(o.getAsInt()) : Optional.empty());
    }
}
