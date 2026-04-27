package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data.action.EntityAction;
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
public class ActionWhenDamageTakenPower extends HasCooldownPower {
    public static final MapCodec<ActionWhenDamageTakenPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            EntityAction.CODEC.fieldOf("entity_action").forGetter(ActionWhenDamageTakenPower::getEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionWhenDamageTakenPower::getDamageCondition)
    ).apply(i, ActionWhenDamageTakenPower::new));
    private final EntityAction entityAction;
    private final DamageCondition damageCondition;

    public ActionWhenDamageTakenPower(BaseSettings settings, CooldownSettings cooldown, EntityAction entityAction, DamageCondition damageCondition) {
        super(settings, cooldown);
        this.entityAction = entityAction;
        this.damageCondition = damageCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Post event) {
        LivingEntity living = event.getEntity();
        OriginDataHolder holder = OriginDataHolder.get(living);
        holder.executePowersWithCondition(ActionWhenDamageTakenPower.class,
                p -> p.damageCondition.test(event.getSource(), event.getNewDamage()),
                p -> p.getCooldownComponent(holder).useIfReady(() -> p.entityAction.execute(living)));
    }
}
