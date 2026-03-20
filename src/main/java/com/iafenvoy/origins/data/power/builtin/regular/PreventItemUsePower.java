package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record PreventItemUsePower(ItemCondition itemCondition,
                                  EntityCondition condition) implements Power {
    public static final MapCodec<PreventItemUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.optionalCodec("item_condition").forGetter(PreventItemUsePower::itemCondition),
            EntityCondition.optionalCodec("condition").forGetter(PreventItemUsePower::condition)
    ).apply(i, PreventItemUsePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
