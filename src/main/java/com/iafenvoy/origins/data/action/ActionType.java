package com.iafenvoy.origins.data.action;

import com.mojang.serialization.MapCodec;

public record ActionType<T>(MapCodec<T> codec) {
}
