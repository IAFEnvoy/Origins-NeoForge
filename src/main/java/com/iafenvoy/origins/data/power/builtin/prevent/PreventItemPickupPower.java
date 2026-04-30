package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

@NotImplementedYet
public class PreventItemPickupPower extends Power implements Prioritized {
    public static final MapCodec<PreventItemPickupPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(PreventItemPickupPower::getSettings),
            BiEntityAction.optionalCodec("bi_entity_action_thrower").forGetter(PreventItemPickupPower::getBiEntityActionThrower),
            BiEntityAction.optionalCodec("bi_entity_action_item").forGetter(PreventItemPickupPower::getBiEntityActionItem),
            ItemAction.optionalCodec("item_action").forGetter(PreventItemPickupPower::getItemAction),
            BiEntityCondition.optionalCodec("bi_entity_condition").forGetter(PreventItemPickupPower::getBiEntityCondition),
            ItemCondition.optionalCodec("item_condition").forGetter(PreventItemPickupPower::getItemCondition),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(PreventItemPickupPower::getPriority)
    ).apply(instance, PreventItemPickupPower::new));
    private final BiEntityAction biEntityActionThrower, biEntityActionItem;
    private final ItemAction itemAction;
    private final BiEntityCondition biEntityCondition;
    private final ItemCondition itemCondition;
    private final int priority;

    public PreventItemPickupPower(BaseSettings settings, BiEntityAction biEntityActionThrower, BiEntityAction biEntityActionItem, ItemAction itemAction, BiEntityCondition biEntityCondition, ItemCondition itemCondition, int priority) {
        super(settings);
        this.biEntityActionThrower = biEntityActionThrower;
        this.biEntityActionItem = biEntityActionItem;
        this.itemAction = itemAction;
        this.biEntityCondition = biEntityCondition;
        this.itemCondition = itemCondition;
        this.priority = priority;
    }

    public BiEntityAction getBiEntityActionThrower() {
        return this.biEntityActionThrower;
    }

    public BiEntityAction getBiEntityActionItem() {
        return this.biEntityActionItem;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
