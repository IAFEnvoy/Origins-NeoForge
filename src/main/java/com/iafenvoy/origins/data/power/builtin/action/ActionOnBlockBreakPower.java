package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record ActionOnBlockBreakPower(BlockCondition blockCondition,EntityAction entityAction,BlockAction blockAction,boolean onlyWhenHarvested)implements Power {

    public static final MapCodec<ActionOnBlockBreakPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(ActionOnBlockBreakPower::blockCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnBlockBreakPower::entityAction),
            BlockAction.optionalCodec("block_action").forGetter(ActionOnBlockBreakPower::blockAction),
            Codec.BOOL.optionalFieldOf("only_when_harvested", true).forGetter(ActionOnBlockBreakPower::onlyWhenHarvested)
    ).apply(i, ActionOnBlockBreakPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
