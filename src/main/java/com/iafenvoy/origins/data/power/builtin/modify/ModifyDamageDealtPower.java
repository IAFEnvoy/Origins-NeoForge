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
import org.jetbrains.annotations.Nullable;

public record ModifyDamageDealtPower(DamageCondition damageCondition, EntityCondition targetCondition,
                                     BiEntityCondition biEntityCondition, EntityAction selfAction, EntityAction targetAction,
                                     BiEntityAction biEntityAction) implements Power {

    public static final MapCodec<ModifyDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(

            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageDealtPower::damageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(ModifyDamageDealtPower::targetCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageDealtPower::biEntityCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageDealtPower::selfAction),
            EntityAction.optionalCodec("target_action").forGetter(ModifyDamageDealtPower::targetAction),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageDealtPower::biEntityAction)
    ).apply(i, ModifyDamageDealtPower::new));

// TODO ListConfiguration

//    public static final Codec<ModifyDamageDealtConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyDamageDealtConfiguration::modifiers),
//            ConfiguredDamageCondition.optional("damage_condition").forGetter(ModifyDamageDealtConfiguration::damageCondition),
//            ConfiguredEntityCondition.optional("target_condition").forGetter(ModifyDamageDealtConfiguration::targetCondition),
//            ConfiguredBiEntityCondition.optional("bientity_condition").forGetter(ModifyDamageDealtConfiguration::biEntityCondition),
//            ConfiguredEntityAction.optional("self_action").forGetter(ModifyDamageDealtConfiguration::selfAction),
//            ConfiguredEntityAction.optional("target_action").forGetter(ModifyDamageDealtConfiguration::targetAction),
//            ConfiguredBiEntityAction.optional("bientity_action").forGetter(ModifyDamageDealtConfiguration::biEntityAction)
//    ).apply(instance, ModifyDamageDealtConfiguration::new));
    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    // TODO
//    public static float modifyMelee(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
//        return PowerContainer.modify(entity, ApoliPowers.MODIFY_DAMAGE_DEALT.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> x.value().getFactory().execute(x.value(), Objects.requireNonNull(entity), target));
//    }
//
//    public static float modifyProjectile(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
//        return PowerContainer.modify(entity, ApoliPowers.MODIFY_PROJECTILE_DAMAGE.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> x.value().getFactory().execute(x.value(), Objects.requireNonNull(entity), target));
//    }
//
//    public static float modifyMeleeNoExec(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
//        return PowerContainer.modify(entity, ApoliPowers.MODIFY_DAMAGE_DEALT.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> {});
//    }
//
//    public static float modifyProjectileNoExec(@Nullable Entity entity, Entity target, DamageSource source, float amount) {
//        return PowerContainer.modify(entity, ApoliPowers.MODIFY_PROJECTILE_DAMAGE.get(), amount, x -> x.value().getFactory().check(x.value(), Objects.requireNonNull(entity), target, source, amount), x -> {});
//    }

    public boolean test(Entity entity, @Nullable Entity target, DamageSource source, float amount) {
        return damageCondition().test(source, amount) &&
                (target == null || targetCondition().test(target)) &&
                (target == null || biEntityCondition().test(entity, target));
    }

    public void execute(Entity entity, @Nullable Entity target) {
        selfAction().execute(entity);
        if (target != null) {
            targetAction().execute(target);
            biEntityAction().execute(entity, target);
        }
    }
}
