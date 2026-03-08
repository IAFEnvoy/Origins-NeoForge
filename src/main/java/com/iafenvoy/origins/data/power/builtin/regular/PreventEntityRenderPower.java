package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record PreventEntityRenderPower(BiEntityCondition bientityCondition,
                                        EntityCondition entityCondition,
                                        EntityCondition condition) implements Power {
    public static final MapCodec<PreventEntityRenderPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(PreventEntityRenderPower::bientityCondition),
            EntityCondition.optionalCodec("entity_condition").forGetter(PreventEntityRenderPower::entityCondition),
            EntityCondition.optionalCodec("condition").forGetter(PreventEntityRenderPower::condition)
    ).apply(i, PreventEntityRenderPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
