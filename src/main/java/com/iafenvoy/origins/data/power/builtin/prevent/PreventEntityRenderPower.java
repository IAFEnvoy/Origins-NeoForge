package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventEntityRenderPower extends Power {
    public static final MapCodec<PreventEntityRenderPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(PreventEntityRenderPower::getBientityCondition),
            EntityCondition.optionalCodec("entity_condition").forGetter(PreventEntityRenderPower::getEntityCondition)
    ).apply(i, PreventEntityRenderPower::new));
    private final BiEntityCondition bientityCondition;
    private final EntityCondition entityCondition;

    public PreventEntityRenderPower(BaseSettings settings, BiEntityCondition bientityCondition, EntityCondition entityCondition) {
        super(settings);
        this.bientityCondition = bientityCondition;
        this.entityCondition = entityCondition;
    }

    public BiEntityCondition getBientityCondition() {
        return this.bientityCondition;
    }

    public EntityCondition getEntityCondition() {
        return this.entityCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
