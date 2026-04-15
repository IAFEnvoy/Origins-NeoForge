package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActionOnItemUsePower extends Power {
    public static final MapCodec<ActionOnItemUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnItemUsePower::getItemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnItemUsePower::getEntityAction),
            ItemAction.optionalCodec("item_action").forGetter(ActionOnItemUsePower::getItemAction)
    ).apply(i, ActionOnItemUsePower::new));
    private final ItemCondition itemCondition;
    private final EntityAction entityAction;
    private final ItemAction itemAction;

    public ActionOnItemUsePower(BaseSettings settings, ItemCondition itemCondition, EntityAction entityAction, ItemAction itemAction) {
        super(settings);
        this.itemCondition = itemCondition;
        this.entityAction = entityAction;
        this.itemAction = itemAction;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
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
