package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ModifyDamageTakenPower(DamageCondition damageCondition, BiEntityCondition biEntityCondition,EntityAction selfAction,
                                     EntityAction targetAction,BiEntityAction biEntityAction,EntityCondition applyArmorCondition,EntityCondition damageArmorCondition) implements Power {

    public static final MapCodec<ModifyDamageTakenPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(

            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageTakenPower::damageCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageTakenPower::biEntityCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageTakenPower::selfAction),
            EntityAction.optionalCodec("attacker_action").forGetter(ModifyDamageTakenPower::targetAction),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageTakenPower::biEntityAction),
            EntityCondition.optionalCodec("apply_armor_condition").forGetter(ModifyDamageTakenPower::applyArmorCondition),
            EntityCondition.optionalCodec("damage_armor_condition").forGetter(ModifyDamageTakenPower::damageArmorCondition)
    ).apply(i, ModifyDamageTakenPower::new));

// TODO ListConfiguration

//    public static final Codec<ModifyDamageTakenConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyDamageTakenConfiguration::modifiers),
//            ConfiguredDamageCondition.optional("damage_condition").forGetter(ModifyDamageTakenConfiguration::damageCondition),
//            ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(ModifyDamageTakenConfiguration::biEntityCondition),
//            ConfiguredEntityAction.optional("self_action").forGetter(ModifyDamageTakenConfiguration::selfAction),
//            ConfiguredEntityAction.optional("attacker_action").forGetter(ModifyDamageTakenConfiguration::targetAction),
//            ConfiguredBiEntityAction.optional("bientity_action").forGetter(ModifyDamageTakenConfiguration::biEntityAction),
//            ConfiguredEntityCondition.optional("apply_armor_condition").forGetter(ModifyDamageTakenConfiguration::applyArmorCondition),
//            ConfiguredEntityCondition.optional("damage_armor_condition").forGetter(ModifyDamageTakenConfiguration::damageArmorCondition)
//    ).apply(instance, ModifyDamageTakenConfiguration::new));
    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    // TODO
//
//    public static float modify(Entity entity, DamageSource source, float amount) {
//        return PowerContainer.modify(entity, ApoliPowers.MODIFY_DAMAGE_TAKEN.get(), amount, x -> x.value().getFactory().check(x.value(), entity, source, amount), x -> x.value().getFactory().execute(x.value(), entity, source));
//    }
//
//    public boolean modifiesArmorApplicance(ConfiguredPower<ModifyDamageTakenConfiguration, ?> config) {
//        return !config.getConfiguration().applyArmorCondition().is(ApoliDefaultConditions.ENTITY_DEFAULT.getId());
//    }
//
//    public boolean checkArmorApplicance(ConfiguredPower<ModifyDamageTakenConfiguration, ?> config, Entity entity) {
//        return !config.getConfiguration().applyArmorCondition().is(ApoliDefaultConditions.ENTITY_DEFAULT.getId()) && ConfiguredEntityCondition.check(config.getConfiguration().applyArmorCondition(), entity);
//    }
//
//    public boolean modifiesArmorDamaging(ConfiguredPower<ModifyDamageTakenConfiguration, ?> config) {
//        return !config.getConfiguration().damageArmorCondition().is(ApoliDefaultConditions.ENTITY_DEFAULT.getId());
//    }
//
//    public boolean checkArmorDamaging(ConfiguredPower<ModifyDamageTakenConfiguration, ?> config, Entity entity) {
//        return !config.getConfiguration().damageArmorCondition().is(ApoliDefaultConditions.ENTITY_DEFAULT.getId()) && ConfiguredEntityCondition.check(config.getConfiguration().damageArmorCondition(), entity);
//    }

    public boolean check(Entity entity, DamageSource source, float amount) {
        boolean damage = damageCondition().test(source, amount);
        if (!damage) return false;
        Entity attacker = source.getEntity();
//        return attacker == null ? biEntityCondition().is(ApoliDefaultConditions.BIENTITY_DEFAULT.getId()) :
//                biEntityCondition().test(source.getEntity(), entity);
        return attacker != null && biEntityCondition().test(source.getEntity(), entity);
    }

    public void execute(Entity entity, DamageSource source) {
        selfAction().execute(entity);
        if (source.getEntity() != null) {
            targetAction().execute(source.getEntity());
            biEntityAction().execute(source.getEntity(), entity);
        }
    }
}
