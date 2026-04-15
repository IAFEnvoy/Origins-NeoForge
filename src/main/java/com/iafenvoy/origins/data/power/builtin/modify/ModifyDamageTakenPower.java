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

import java.util.List;

public class ModifyDamageTakenPower extends Power {
    public static final MapCodec<ModifyDamageTakenPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyDamageTakenPower::getModifiers),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageTakenPower::getDamageCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageTakenPower::getBiEntityCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageTakenPower::getSelfAction),
            EntityAction.optionalCodec("attacker_action").forGetter(ModifyDamageTakenPower::getTargetAction),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageTakenPower::getBiEntityAction),
            EntityCondition.optionalCodec("apply_armor_condition").forGetter(ModifyDamageTakenPower::getApplyArmorCondition),
            EntityCondition.optionalCodec("damage_armor_condition").forGetter(ModifyDamageTakenPower::getDamageArmorCondition)
    ).apply(i, ModifyDamageTakenPower::new));

    private final List<Modifier> modifiers;
    private final DamageCondition damageCondition;
    private final BiEntityCondition biEntityCondition;
    private final EntityAction selfAction;
    private final EntityAction targetAction;
    private final BiEntityAction biEntityAction;
    private final EntityCondition applyArmorCondition;
    private final EntityCondition damageArmorCondition;

    public ModifyDamageTakenPower(BaseSettings settings, List<Modifier> modifiers, DamageCondition damageCondition, BiEntityCondition biEntityCondition, EntityAction selfAction, EntityAction targetAction, BiEntityAction biEntityAction, EntityCondition applyArmorCondition, EntityCondition damageArmorCondition) {
        super(settings);
        this.modifiers = modifiers;
        this.damageCondition = damageCondition;
        this.biEntityCondition = biEntityCondition;
        this.selfAction = selfAction;
        this.targetAction = targetAction;
        this.biEntityAction = biEntityAction;
        this.applyArmorCondition = applyArmorCondition;
        this.damageArmorCondition = damageArmorCondition;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
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

    public EntityCondition getApplyArmorCondition() {
        return this.applyArmorCondition;
    }

    public EntityCondition getDamageArmorCondition() {
        return this.damageArmorCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean check(Entity entity, DamageSource source, float amount) {
        if (!this.getDamageCondition().test(source, amount)) return false;
        Entity attacker = source.getEntity();
        return attacker != null && this.getBiEntityCondition().test(source.getEntity(), entity);
    }

    public void execute(Entity entity, DamageSource source) {
        this.getSelfAction().execute(entity);
        if (source.getEntity() != null) {
            this.getTargetAction().execute(source.getEntity());
            this.getBiEntityAction().execute(source.getEntity(), entity);
        }
    }

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }
}
