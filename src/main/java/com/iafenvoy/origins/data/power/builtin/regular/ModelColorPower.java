package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record ModelColorPower(float red, float green, float blue, float alpha,
                              EntityCondition condition) implements Power {
    public static final MapCodec<ModelColorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("red", 1F).forGetter(ModelColorPower::red),
            Codec.FLOAT.optionalFieldOf("green", 1F).forGetter(ModelColorPower::green),
            Codec.FLOAT.optionalFieldOf("blue", 1F).forGetter(ModelColorPower::blue),
            Codec.FLOAT.optionalFieldOf("alpha", 1F).forGetter(ModelColorPower::alpha),
            EntityCondition.optionalCodec("condition").forGetter(ModelColorPower::condition)
    ).apply(i, ModelColorPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
