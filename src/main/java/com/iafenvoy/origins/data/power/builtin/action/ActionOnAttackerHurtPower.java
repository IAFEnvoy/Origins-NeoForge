package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnAttackerHurtPower extends HasCooldownPower {
    public static final MapCodec<ActionOnAttackerHurtPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(ActionOnAttackerHurtPower::getCooldown),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionOnAttackerHurtPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnAttackerHurtPower::getBiEntityCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionOnAttackerHurtPower::getDamageCondition)
    ).apply(instance, ActionOnAttackerHurtPower::new));

    private final BiEntityAction bientityAction;
    private final BiEntityCondition bientityCondition;
    private final DamageCondition damageCondition;

    public ActionOnAttackerHurtPower(BaseSettings settings, CooldownSettings cooldown, BiEntityAction bientityAction, BiEntityCondition bientityCondition, DamageCondition damageCondition) {
        super(settings, cooldown);
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.damageCondition = damageCondition;
    }

    public BiEntityAction getBiEntityAction() {
        return this.bientityAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.bientityCondition;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        LivingEntity holder = event.getEntity();
        LivingEntity attacker = holder.getLastHurtByMob();
        if (attacker == null)
            return;
        PowerHelper.get(holder).execute(ActionOnAttackerHurtPower.class,
                power -> power.damageCondition.test(event.getSource(), event.getNewDamage()) && power.bientityCondition.test(holder, attacker),
                (data, power) -> power.getCooldownComponent(data).useIfReady(() -> power.bientityAction.execute(holder, attacker)));
    }
}
