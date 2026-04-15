package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActionOnBlockBreakPower extends Power {
    public static final MapCodec<ActionOnBlockBreakPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(ActionOnBlockBreakPower::getBlockCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnBlockBreakPower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ActionOnBlockBreakPower::getBlockAction),
            Codec.BOOL.optionalFieldOf("only_when_harvested", true).forGetter(ActionOnBlockBreakPower::isOnlyWhenHarvested)
    ).apply(i, ActionOnBlockBreakPower::new));
    private final BlockCondition blockCondition;
    private final EntityAction entityAction;
    private final BlockAction blockAction;
    private final boolean onlyWhenHarvested;

    public ActionOnBlockBreakPower(BaseSettings settings, BlockCondition blockCondition, EntityAction entityAction, BlockAction blockAction, boolean onlyWhenHarvested) {
        super(settings);
        this.blockCondition = blockCondition;
        this.entityAction = entityAction;
        this.blockAction = blockAction;
        this.onlyWhenHarvested = onlyWhenHarvested;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public boolean isOnlyWhenHarvested() {
        return this.onlyWhenHarvested;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
