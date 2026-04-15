package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActionOverTimePower extends Power {
    public static final MapCodec<ActionOverTimePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOverTimePower::getEntityAction),
            EntityAction.optionalCodec("rising_action").forGetter(ActionOverTimePower::getRisingAction),
            EntityAction.optionalCodec("falling_action").forGetter(ActionOverTimePower::getFallingAction),
            Codec.INT.optionalFieldOf("interval", 20).forGetter(ActionOverTimePower::getInterval)
    ).apply(i, ActionOverTimePower::new));
    private final EntityAction entityAction;
    private final EntityAction risingAction;
    private final EntityAction fallingAction;
    private final int interval;

    public ActionOverTimePower(BaseSettings settings, EntityAction entityAction, EntityAction risingAction, EntityAction fallingAction, int interval) {
        super(settings);
        this.entityAction = entityAction;
        this.risingAction = risingAction;
        this.fallingAction = fallingAction;
        this.interval = interval;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public EntityAction getRisingAction() {
        return this.risingAction;
    }

    public EntityAction getFallingAction() {
        return this.fallingAction;
    }

    public int getInterval() {
        return this.interval;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
