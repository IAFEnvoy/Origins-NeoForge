package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventEntityCollisionPower extends Power {
    public static final MapCodec<PreventEntityCollisionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(PreventEntityCollisionPower::getBiEntityCondition)
    ).apply(i, PreventEntityCollisionPower::new));
    private final BiEntityCondition biEntityCondition;

    protected PreventEntityCollisionPower(BaseSettings settings, BiEntityCondition biEntityCondition) {
        super(settings);
        this.biEntityCondition = biEntityCondition;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
