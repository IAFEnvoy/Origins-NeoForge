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

public class ActionOnHitPower extends Power {
    public static final MapCodec<ActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionOnHitPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnHitPower::getBiEntityCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionOnHitPower::getDamageCondition),
            CooldownSettings.CODEC.forGetter(ActionOnHitPower::getCooldown)
    ).apply(i, ActionOnHitPower::new));
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;
    private final DamageCondition damageCondition;
    private final CooldownSettings cooldown;

    public ActionOnHitPower(BaseSettings settings, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, DamageCondition damageCondition, CooldownSettings cooldown) {
        super(settings);
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.damageCondition = damageCondition;
        this.cooldown = cooldown;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    public CooldownSettings getCooldown() {
        return this.cooldown;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public List<PowerComponent> createComponents() {
        return List.of(new CooldownComponent(this.getCooldown().cooldown()));
    }
}