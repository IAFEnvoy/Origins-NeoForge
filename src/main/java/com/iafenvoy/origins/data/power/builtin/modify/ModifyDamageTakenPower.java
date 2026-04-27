package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ModifyDamageTakenPower extends Power {
    public static final MapCodec<ModifyDamageTakenPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyDamageTakenPower::getModifiers),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageTakenPower::getBiEntityAction),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageTakenPower::getSelfAction),
            EntityAction.optionalCodec("attacker_action").forGetter(ModifyDamageTakenPower::getAttackerAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageTakenPower::getBiEntityCondition),
            EntityCondition.optionalCodec("apply_armor_condition").forGetter(ModifyDamageTakenPower::getApplyArmorCondition),
            EntityCondition.optionalCodec("damage_armor_condition").forGetter(ModifyDamageTakenPower::getDamageArmorCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageTakenPower::getDamageCondition)
    ).apply(i, ModifyDamageTakenPower::new));

    private final List<Modifier> modifiers;
    private final BiEntityAction biEntityAction;
    private final EntityAction selfAction, attackerAction;
    private final BiEntityCondition biEntityCondition;
    //FIXME::Implement these 2 conditions
    private final EntityCondition applyArmorCondition, damageArmorCondition;
    private final DamageCondition damageCondition;

    public ModifyDamageTakenPower(BaseSettings settings, List<Modifier> modifiers, BiEntityAction biEntityAction, EntityAction selfAction, EntityAction attackerAction, BiEntityCondition biEntityCondition, EntityCondition applyArmorCondition, EntityCondition damageArmorCondition, DamageCondition damageCondition) {
        super(settings);
        this.modifiers = modifiers;
        this.biEntityAction = biEntityAction;
        this.selfAction = selfAction;
        this.attackerAction = attackerAction;
        this.biEntityCondition = biEntityCondition;
        this.applyArmorCondition = applyArmorCondition;
        this.damageArmorCondition = damageArmorCondition;
        this.damageCondition = damageCondition;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    public EntityAction getSelfAction() {
        return this.selfAction;
    }

    public EntityAction getAttackerAction() {
        return this.attackerAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public EntityCondition getApplyArmorCondition() {
        return this.applyArmorCondition;
    }

    public EntityCondition getDamageArmorCondition() {
        return this.damageArmorCondition;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean check(Entity entity, DamageSource source, float amount) {
        if (!this.damageCondition.test(source, amount)) return false;
        Entity attacker = source.getEntity();
        return attacker != null && this.biEntityCondition.test(source.getEntity(), entity);
    }

    public void execute(Entity entity, DamageSource source) {
        this.selfAction.execute(entity);
        if (source.getEntity() != null) {
            this.attackerAction.execute(source.getEntity());
            this.biEntityAction.execute(source.getEntity(), entity);
        }
    }

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        Entity source = event.getSource().getEntity(), target = event.getEntity();
        if (source == null) return;
        OriginDataHolder.get(target).streamActivePowers(ModifyDamageTakenPower.class).forEach(power -> {
            float baseValue = event.getNewDamage();
            DamageSource s = event.getSource();
            if (power.biEntityCondition.test(source, target) && power.damageCondition.test(s, baseValue)) {
                event.setNewDamage((float) Modifier.applyModifiers(power.modifiers, baseValue));
                power.selfAction.execute(target);
                power.attackerAction.execute(source);
                power.biEntityAction.execute(source, target);
            }
        });
    }
}
