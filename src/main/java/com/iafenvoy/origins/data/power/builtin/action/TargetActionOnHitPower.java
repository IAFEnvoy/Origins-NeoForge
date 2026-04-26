package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.common.CooldownSettings;
import com.iafenvoy.origins.data.common.HudRender;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.HudRenderable;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

//FIXME::Merge with ActionOnHitPower
@EventBusSubscriber
public class TargetActionOnHitPower extends HasCooldownPower {
    public static final MapCodec<TargetActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(TargetActionOnHitPower::getCooldown),
            EntityAction.optionalCodec("entity_action").forGetter(TargetActionOnHitPower::getEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(TargetActionOnHitPower::getDamageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(TargetActionOnHitPower::getTargetCondition)
    ).apply(i, TargetActionOnHitPower::new));
    private final EntityAction entityAction;
    private final DamageCondition damageCondition;
    private final EntityCondition targetCondition;

    public TargetActionOnHitPower(BaseSettings settings, CooldownSettings cooldown, EntityAction entityAction, DamageCondition damageCondition, EntityCondition targetCondition) {
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
    public static void onDamage(LivingDamageEvent.Post event) {
        Entity source = event.getSource().getEntity(), target = event.getEntity();
        if (source == null) return;
        OriginDataHolder holder = OriginDataHolder.get(source);
        holder.streamActivePowers(TargetActionOnHitPower.class).forEach(power -> {
            if (power.getTargetCondition().test(target) && power.getDamageCondition().test(event.getSource(), event.getNewDamage()))
                holder.getComponentFor(power, CooldownComponent.class).ifPresent(c -> c.useIfReady(() -> power.getEntityAction().execute(target)));
        });
    }
}
