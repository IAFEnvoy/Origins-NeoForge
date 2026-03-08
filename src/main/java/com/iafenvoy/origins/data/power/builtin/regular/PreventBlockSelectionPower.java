package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record PreventBlockSelectionPower(BlockCondition blockCondition,
                                         EntityCondition condition) implements Power {
    public static final MapCodec<PreventBlockSelectionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(PreventBlockSelectionPower::blockCondition),
            EntityCondition.optionalCodec("condition").forGetter(PreventBlockSelectionPower::condition)
    ).apply(i, PreventBlockSelectionPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
