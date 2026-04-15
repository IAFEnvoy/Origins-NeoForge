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

public class TargetActionOnHitPower extends Power {
    public static final MapCodec<TargetActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(TargetActionOnHitPower::getCooldown),
            EntityAction.optionalCodec("entity_action").forGetter(TargetActionOnHitPower::getEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(TargetActionOnHitPower::getDamageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(TargetActionOnHitPower::getTargetCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(TargetActionOnHitPower::getBientityCondition),
            EntityCondition.optionalCodec("condition").forGetter(TargetActionOnHitPower::getCondition)
    ).apply(i, TargetActionOnHitPower::new));
    private final int cooldown;
    private final EntityAction entityAction;
    private final DamageCondition damageCondition;
    private final EntityCondition targetCondition;
    private final BiEntityCondition bientityCondition;
    private final EntityCondition condition;

    public TargetActionOnHitPower(BaseSettings settings, int cooldown, EntityAction entityAction, DamageCondition damageCondition, EntityCondition targetCondition, BiEntityCondition bientityCondition, EntityCondition condition) {
        super(settings);
        this.cooldown = cooldown;
        this.entityAction = entityAction;
        this.damageCondition = damageCondition;
        this.targetCondition = targetCondition;
        this.bientityCondition = bientityCondition;
        this.condition = condition;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    public EntityCondition getTargetCondition() {
        return this.targetCondition;
    }

    public BiEntityCondition getBientityCondition() {
        return this.bientityCondition;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
