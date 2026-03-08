package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyDamageTakenPower(List<Modifier> modifiers,
                                     DamageCondition damageCondition, BiEntityCondition biEntityCondition,
                                     EntityAction selfAction, EntityAction targetAction,
                                     BiEntityAction biEntityAction, EntityCondition applyArmorCondition,
                                     EntityCondition damageArmorCondition) implements Power {

    public static final MapCodec<ModifyDamageTakenPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyDamageTakenPower::modifiers),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageTakenPower::damageCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageTakenPower::biEntityCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageTakenPower::selfAction),
            EntityAction.optionalCodec("attacker_action").forGetter(ModifyDamageTakenPower::targetAction),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageTakenPower::biEntityAction),
            EntityCondition.optionalCodec("apply_armor_condition").forGetter(ModifyDamageTakenPower::applyArmorCondition),
            EntityCondition.optionalCodec("damage_armor_condition").forGetter(ModifyDamageTakenPower::damageArmorCondition)
    ).apply(i, ModifyDamageTakenPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean check(Entity entity, DamageSource source, float amount) {
        if (!damageCondition().test(source, amount)) return false;
        Entity attacker = source.getEntity();
        return attacker != null && biEntityCondition().test(source.getEntity(), entity);
    }

    public void execute(Entity entity, DamageSource source) {
        selfAction().execute(entity);
        if (source.getEntity() != null) {
            targetAction().execute(source.getEntity());
            biEntityAction().execute(source.getEntity(), entity);
        }
    }

    public double apply(double baseValue) {
        return ModifierUtil.applyModifiers(modifiers, baseValue);
    }
}
