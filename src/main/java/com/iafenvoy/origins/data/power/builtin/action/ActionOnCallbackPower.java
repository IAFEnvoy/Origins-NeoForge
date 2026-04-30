package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

@NotImplementedYet
public class ActionOnCallbackPower extends Power {
    public static final MapCodec<ActionOnCallbackPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action_chosen").forGetter(ActionOnCallbackPower::getEntityActionChosen),
            Codec.BOOL.optionalFieldOf("execute_chosen_when_orb", true).forGetter(ActionOnCallbackPower::isExecuteChosenWhenOrb),
            EntityAction.optionalCodec("entity_action_respawned").forGetter(ActionOnCallbackPower::getEntityActionRespawned),
            EntityAction.optionalCodec("entity_action_removed").forGetter(ActionOnCallbackPower::getEntityActionRemoved),
            EntityAction.optionalCodec("entity_action_gained").forGetter(ActionOnCallbackPower::getEntityActionGained),
            EntityAction.optionalCodec("entity_action_lost").forGetter(ActionOnCallbackPower::getEntityActionLost),
            EntityAction.optionalCodec("entity_action_added").forGetter(ActionOnCallbackPower::getEntityActionAdded)
    ).apply(i, ActionOnCallbackPower::new));
    private final EntityAction entityActionChosen;
    private final boolean executeChosenWhenOrb;
    private final EntityAction entityActionRespawned;
    private final EntityAction entityActionRemoved;
    private final EntityAction entityActionGained;
    private final EntityAction entityActionLost;
    private final EntityAction entityActionAdded;

    public ActionOnCallbackPower(BaseSettings settings, EntityAction entityActionChosen, boolean executeChosenWhenOrb, EntityAction entityActionRespawned, EntityAction entityActionRemoved, EntityAction entityActionGained, EntityAction entityActionLost, EntityAction entityActionAdded) {
        super(settings);
        this.entityActionChosen = entityActionChosen;
        this.executeChosenWhenOrb = executeChosenWhenOrb;
        this.entityActionRespawned = entityActionRespawned;
        this.entityActionRemoved = entityActionRemoved;
        this.entityActionGained = entityActionGained;
        this.entityActionLost = entityActionLost;
        this.entityActionAdded = entityActionAdded;
    }

    public EntityAction getEntityActionChosen() {
        return this.entityActionChosen;
    }

    public boolean isExecuteChosenWhenOrb() {
        return this.executeChosenWhenOrb;
    }

    public EntityAction getEntityActionRespawned() {
        return this.entityActionRespawned;
    }

    public EntityAction getEntityActionRemoved() {
        return this.entityActionRemoved;
    }

    public EntityAction getEntityActionGained() {
        return this.entityActionGained;
    }

    public EntityAction getEntityActionLost() {
        return this.entityActionLost;
    }

    public EntityAction getEntityActionAdded() {
        return this.entityActionAdded;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
