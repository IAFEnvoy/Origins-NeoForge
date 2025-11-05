package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record ActionWhenHitPower(DamageCondition damageCondition,EntityAction entityAction) implements Power {


    public static final MapCodec<ActionWhenHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionWhenHitPower::damageCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionWhenHitPower::entityAction)
    ).apply(i, ActionWhenHitPower::new));

// TODO implement ICooldownPowerConfiguration

//    public static final Codec<ActionWhenHitConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ICooldownPowerConfiguration.MAP_CODEC.forGetter(ActionWhenHitConfiguration::cooldown),
//            ConfiguredDamageCondition.optional("damage_condition").forGetter(ActionWhenHitConfiguration::damageCondition),
//            ConfiguredEntityAction.required("entity_action").forGetter(ActionWhenHitConfiguration::entityAction)
//    ).apply(instance, ActionWhenHitConfiguration::new));


    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}