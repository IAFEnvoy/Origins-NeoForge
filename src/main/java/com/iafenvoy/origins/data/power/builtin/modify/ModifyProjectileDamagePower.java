package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ModifyProjectileDamagePower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyProjectileDamagePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(ModifyProjectileDamagePower::getSettings),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyProjectileDamagePower::getDamageCondition),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyProjectileDamagePower::getModifier),
            EntityCondition.optionalCodec("target_condition").forGetter(ModifyProjectileDamagePower::getTargetCondition),
            EntityAction.optionalCodec("self_action").forGetter(ModifyProjectileDamagePower::getSelfAction),
            EntityAction.optionalCodec("target_action").forGetter(ModifyProjectileDamagePower::getTargetAction)
    ).apply(i, ModifyProjectileDamagePower::new));
    private final DamageCondition damageCondition;
    private final List<Modifier> modifier;
    private final EntityCondition targetCondition;
    private final EntityAction selfAction, targetAction;

    public ModifyProjectileDamagePower(BaseSettings settings, DamageCondition damageCondition, List<Modifier> modifier, EntityCondition targetCondition, EntityAction selfAction, EntityAction targetAction) {
        super(settings);
        this.damageCondition = damageCondition;
        this.modifier = modifier;
        this.targetCondition = targetCondition;
        this.selfAction = selfAction;
        this.targetAction = targetAction;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    @Override
    public List<Modifier> getModifier() {
        return this.modifier;
    }

    public EntityCondition getTargetCondition() {
        return this.targetCondition;
    }

    public EntityAction getSelfAction() {
        return this.selfAction;
    }

    public EntityAction getTargetAction() {
        return this.targetAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        DamageSource damageSource = event.getSource();
        Entity source = damageSource.getEntity(), target = event.getEntity();
        if (source != null && damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
            float amount = event.getNewDamage();
            OriginDataHolder holder = OriginDataHolder.get(source);
            List<ModifyProjectileDamagePower> powers = holder.streamActivePowers(ModifyProjectileDamagePower.class).filter(x -> x.damageCondition.test(damageSource, amount) && x.targetCondition.test(target)).toList();
            powers.forEach(x -> {
                x.selfAction.execute(source);
                x.targetAction.execute(target);
            });
            event.setNewDamage(powers.stream().reduce(amount, (p, c) -> Modifier.applyModifiers(holder, c.modifier, p), Float::sum));
        }
    }
}
