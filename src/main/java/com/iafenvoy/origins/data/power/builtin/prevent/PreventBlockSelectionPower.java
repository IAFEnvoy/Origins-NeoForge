package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventBlockSelectionPower extends Power {
    public static final MapCodec<PreventBlockSelectionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(PreventBlockSelectionPower::getBlockCondition),
            EntityCondition.optionalCodec("condition").forGetter(PreventBlockSelectionPower::getCondition)
    ).apply(i, PreventBlockSelectionPower::new));
    private final BlockCondition blockCondition;
    private final EntityCondition condition;

    public PreventBlockSelectionPower(BaseSettings settings, BlockCondition blockCondition, EntityCondition condition) {
        super(settings);
        this.blockCondition = blockCondition;
        this.condition = condition;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
