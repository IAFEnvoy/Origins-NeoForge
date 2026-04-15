package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.common.CooldownSettings;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ActionOnHitPower(BiEntityAction biEntityAction, BiEntityCondition biEntityCondition,
                               DamageCondition damageCondition, CooldownSettings cooldown) implements Power {
    public static final MapCodec<ActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionOnHitPower::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnHitPower::biEntityCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionOnHitPower::damageCondition),
            CooldownSettings.CODEC.forGetter(ActionOnHitPower::cooldown)
    ).apply(i, ActionOnHitPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public List<PowerComponent> createComponents() {
        return List.of(new CooldownComponent(this.cooldown().cooldown()));
    }
}