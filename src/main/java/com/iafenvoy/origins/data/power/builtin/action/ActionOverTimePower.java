package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record ActionOverTimePower(EntityAction entityAction, EntityAction risingAction, EntityAction fallingAction,int interval) implements Power {

    public static final MapCodec<ActionOverTimePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("entity_action").forGetter(ActionOverTimePower::entityAction),
            EntityAction.optionalCodec("rising_action").forGetter(ActionOverTimePower::risingAction),
            EntityAction.optionalCodec("falling_action").forGetter(ActionOverTimePower::fallingAction),
            Codec.INT.optionalFieldOf("interval", 20).forGetter(ActionOverTimePower::interval)
    ).apply(i, ActionOverTimePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}