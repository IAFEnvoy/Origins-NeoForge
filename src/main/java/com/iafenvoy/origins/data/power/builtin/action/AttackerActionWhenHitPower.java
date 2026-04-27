package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class AttackerActionWhenHitPower extends HasCooldownPower {
    public static final MapCodec<AttackerActionWhenHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(AttackerActionWhenHitPower::getCooldown),
            EntityAction.CODEC.fieldOf("entity_action").forGetter(AttackerActionWhenHitPower::getEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(AttackerActionWhenHitPower::getDamageCondition)
    ).apply(i, AttackerActionWhenHitPower::new));
    private final EntityAction entityAction;
    private final DamageCondition damageCondition;

    protected AttackerActionWhenHitPower(BaseSettings settings, CooldownSettings cooldown, EntityAction entityAction, DamageCondition damageCondition) {
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
        Entity entity = event.getEntity(), source = event.getSource().getEntity();
        if (source == null) return;
        OriginDataHolder holder = OriginDataHolder.get(entity);
        holder.executePowersWithCondition(AttackerActionWhenHitPower.class,
                p -> p.getDamageCondition().test(event.getSource(), event.getNewDamage()),
                p -> p.getCooldownComponent(holder).useIfReady(() -> p.getEntityAction().execute(source)));
    }
}
