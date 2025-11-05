package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record ActionOnItemUsePower(ItemCondition itemCondition, EntityAction entityAction, ItemAction itemAction) implements Power {

    public static final MapCodec<ActionOnItemUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnItemUsePower::itemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnItemUsePower::entityAction),
            ItemAction.optionalCodec("item_action").forGetter(ActionOnItemUsePower::itemAction)
    ).apply(i, ActionOnItemUsePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
