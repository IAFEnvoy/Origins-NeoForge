package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record ActionOnWakeUpPower(BlockCondition blockCondition, EntityAction entityAction, ItemAction itemAction) implements Power {

    public static final MapCodec<ActionOnWakeUpPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("item_condition").forGetter(ActionOnWakeUpPower::blockCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnWakeUpPower::entityAction),
            ItemAction.optionalCodec("item_action").forGetter(ActionOnWakeUpPower::itemAction)
    ).apply(i, ActionOnWakeUpPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}