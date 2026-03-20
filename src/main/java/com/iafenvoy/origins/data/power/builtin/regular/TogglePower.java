package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record TogglePower(boolean activeByDefault, boolean retainState,
                          EntityCondition condition) implements Power {
    public static final MapCodec<TogglePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("active_by_default", true).forGetter(TogglePower::activeByDefault),
            Codec.BOOL.optionalFieldOf("retain_state", true).forGetter(TogglePower::retainState),
            EntityCondition.optionalCodec("condition").forGetter(TogglePower::condition)
    ).apply(i, TogglePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
