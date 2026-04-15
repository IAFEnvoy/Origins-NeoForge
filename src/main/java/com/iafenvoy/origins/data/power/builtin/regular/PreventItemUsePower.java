package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventItemUsePower extends Power {
    public static final MapCodec<PreventItemUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(PreventItemUsePower::getItemCondition),
            EntityCondition.optionalCodec("condition").forGetter(PreventItemUsePower::getCondition)
    ).apply(i, PreventItemUsePower::new));
    private final ItemCondition itemCondition;
    private final EntityCondition condition;

    public PreventItemUsePower(BaseSettings settings, ItemCondition itemCondition, EntityCondition condition) {
        super(settings);
        this.itemCondition = itemCondition;
        this.condition = condition;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
