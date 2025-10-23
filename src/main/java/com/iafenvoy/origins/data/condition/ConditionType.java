package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.MapCodec;

public record ConditionType<T>(MapCodec<T> codec) {
}
