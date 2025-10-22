package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record ConditionType(MapCodec<? extends Condition> codec) {
    public static final Codec<Condition> CODEC = ConditionRegistries.CONDITION.byNameCodec().dispatch("type", Condition::type, ConditionType::codec);
}
