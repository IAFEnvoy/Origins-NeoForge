package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActionOnWakeUpPower extends Power {
    public static final MapCodec<ActionOnWakeUpPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("item_condition").forGetter(ActionOnWakeUpPower::getBlockCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnWakeUpPower::getEntityAction),
            ItemAction.optionalCodec("item_action").forGetter(ActionOnWakeUpPower::getItemAction)
    ).apply(i, ActionOnWakeUpPower::new));
    private final BlockCondition blockCondition;
    private final EntityAction entityAction;
    private final ItemAction itemAction;

    public ActionOnWakeUpPower(BaseSettings settings, BlockCondition blockCondition, EntityAction entityAction, ItemAction itemAction) {
        super(settings);
        this.blockCondition = blockCondition;
        this.entityAction = entityAction;
        this.itemAction = itemAction;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}