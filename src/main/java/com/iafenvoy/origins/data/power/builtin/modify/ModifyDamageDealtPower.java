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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ModifyDamageDealtPower(List<Modifier> modifiers,
                                     DamageCondition damageCondition, EntityCondition targetCondition,
                                     BiEntityCondition biEntityCondition, EntityAction selfAction,
                                     EntityAction targetAction, BiEntityAction biEntityAction) implements Power {

    public static final MapCodec<ModifyDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyDamageDealtPower::modifiers),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageDealtPower::damageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(ModifyDamageDealtPower::targetCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageDealtPower::biEntityCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageDealtPower::selfAction),
            EntityAction.optionalCodec("target_action").forGetter(ModifyDamageDealtPower::targetAction),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageDealtPower::biEntityAction)
    ).apply(i, ModifyDamageDealtPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

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

    public double apply(double baseValue) {
        return ModifierUtil.applyModifiers(modifiers, baseValue);
    }
}
