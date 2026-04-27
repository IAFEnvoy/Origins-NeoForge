package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class SelfActionOnHitPower extends HasCooldownPower {
    public static final MapCodec<SelfActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            EntityAction.CODEC.fieldOf("entity_action").forGetter(SelfActionOnHitPower::getEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(SelfActionOnHitPower::getDamageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(SelfActionOnHitPower::getTargetCondition)
    ).apply(i, SelfActionOnHitPower::new));
    private final EntityAction entityAction;
    private final DamageCondition damageCondition;
    private final EntityCondition targetCondition;

    protected SelfActionOnHitPower(BaseSettings settings, CooldownSettings cooldown, EntityAction entityAction, DamageCondition damageCondition, EntityCondition targetCondition) {
        super(settings, cooldown);
        this.entityAction = entityAction;
        this.damageCondition = damageCondition;
        this.targetCondition = targetCondition;
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

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        Entity self = event.getSource().getEntity(), target = event.getEntity();
        if (self == null) return;
        OriginDataHolder holder = OriginDataHolder.get(self);
        holder.executePowersWithCondition(SelfActionOnHitPower.class,
                p -> p.damageCondition.test(event.getSource(), event.getNewDamage()) && p.targetCondition.test(target),
                p -> p.getCooldownComponent(holder).useIfReady(() -> p.entityAction.execute(self)));
    }
}
