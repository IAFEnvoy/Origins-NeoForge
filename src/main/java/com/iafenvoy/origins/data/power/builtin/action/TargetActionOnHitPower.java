package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record TargetActionOnHitPower(int cooldown, EntityAction entityAction,
                                      DamageCondition damageCondition,
                                      EntityCondition targetCondition,
                                      BiEntityCondition bientityCondition,
                                      EntityCondition condition) implements Power {
    public static final MapCodec<TargetActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(TargetActionOnHitPower::cooldown),
            EntityAction.optionalCodec("entity_action").forGetter(TargetActionOnHitPower::entityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(TargetActionOnHitPower::damageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(TargetActionOnHitPower::targetCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(TargetActionOnHitPower::bientityCondition),
            EntityCondition.optionalCodec("condition").forGetter(TargetActionOnHitPower::condition)
    ).apply(i, TargetActionOnHitPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
