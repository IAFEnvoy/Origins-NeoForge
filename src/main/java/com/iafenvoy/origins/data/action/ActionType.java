package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record ActionType(MapCodec<? extends Action> codec) {
    public static final Codec<Action> CODEC = ActionRegistries.ACTION.byNameCodec().dispatch("type", Action::type, ActionType::codec);
}
