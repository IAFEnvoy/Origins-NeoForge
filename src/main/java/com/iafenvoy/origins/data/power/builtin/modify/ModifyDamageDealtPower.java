package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyDamageDealtPower extends Power {
    public static final MapCodec<ModifyDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyDamageDealtPower::getModifiers),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageDealtPower::getDamageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(ModifyDamageDealtPower::getTargetCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageDealtPower::getBiEntityCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageDealtPower::getSelfAction),
            EntityAction.optionalCodec("target_action").forGetter(ModifyDamageDealtPower::getTargetAction),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageDealtPower::getBiEntityAction)
    ).apply(i, ModifyDamageDealtPower::new));

    private final List<Modifier> modifiers;
    private final DamageCondition damageCondition;
    private final EntityCondition targetCondition;
    private final BiEntityCondition biEntityCondition;
    private final EntityAction selfAction;
    private final EntityAction targetAction;
    private final BiEntityAction biEntityAction;

    public ModifyDamageDealtPower(BaseSettings settings, List<Modifier> modifiers,
                                  DamageCondition damageCondition, EntityCondition targetCondition,
                                  BiEntityCondition biEntityCondition, EntityAction selfAction,
                                  EntityAction targetAction, BiEntityAction biEntityAction) {
        super(settings);
        this.modifiers = modifiers;
        this.damageCondition = damageCondition;
        this.targetCondition = targetCondition;
        this.biEntityCondition = biEntityCondition;
        this.selfAction = selfAction;
        this.targetAction = targetAction;
        this.biEntityAction = biEntityAction;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    public EntityCondition getTargetCondition() {
        return this.targetCondition;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public EntityAction getSelfAction() {
        return this.selfAction;
    }

    public EntityAction getTargetAction() {
        return this.targetAction;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean test(Entity entity, @Nullable Entity target, DamageSource source, float amount) {
        return this.getDamageCondition().test(source, amount) &&
                (target == null || this.getTargetCondition().test(target)) &&
                (target == null || this.getBiEntityCondition().test(entity, target));
    }

    public void execute(Entity entity, @Nullable Entity target) {
        this.getSelfAction().execute(entity);
        if (target != null) {
            this.getTargetAction().execute(target);
            this.getBiEntityAction().execute(entity, target);
        }
    }

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }
}
