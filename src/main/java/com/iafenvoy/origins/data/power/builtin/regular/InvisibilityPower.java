package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record InvisibilityPower(boolean renderArmor,
                                EntityCondition condition) implements Power {
    public static final MapCodec<InvisibilityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("render_armor", true).forGetter(InvisibilityPower::renderArmor),
            EntityCondition.optionalCodec("condition").forGetter(InvisibilityPower::condition)
    ).apply(i, InvisibilityPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
