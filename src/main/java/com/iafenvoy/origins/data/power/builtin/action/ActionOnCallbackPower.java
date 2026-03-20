package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record ActionOnCallbackPower(
        EntityAction entityActionChosen,
        boolean executeChosenWhenOrb,
        EntityAction entityActionRespawned,
        EntityAction entityActionRemoved,
        EntityAction entityActionGained,
        EntityAction entityActionLost,
        EntityAction entityActionAdded,
        EntityCondition condition
) implements Power {
    public static final MapCodec<ActionOnCallbackPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("entity_action_chosen").forGetter(ActionOnCallbackPower::entityActionChosen),
            Codec.BOOL.optionalFieldOf("execute_chosen_when_orb", true).forGetter(ActionOnCallbackPower::executeChosenWhenOrb),
            EntityAction.optionalCodec("entity_action_respawned").forGetter(ActionOnCallbackPower::entityActionRespawned),
            EntityAction.optionalCodec("entity_action_removed").forGetter(ActionOnCallbackPower::entityActionRemoved),
            EntityAction.optionalCodec("entity_action_gained").forGetter(ActionOnCallbackPower::entityActionGained),
            EntityAction.optionalCodec("entity_action_lost").forGetter(ActionOnCallbackPower::entityActionLost),
            EntityAction.optionalCodec("entity_action_added").forGetter(ActionOnCallbackPower::entityActionAdded),
            EntityCondition.optionalCodec("condition").forGetter(ActionOnCallbackPower::condition)
    ).apply(i, ActionOnCallbackPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
