package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.common.CooldownSettings;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ActionOnHitPower extends Power {
    public static final MapCodec<ActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionOnHitPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnHitPower::getBiEntityCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionOnHitPower::getDamageCondition),
            CooldownSettings.CODEC.forGetter(ActionOnHitPower::getCooldown)
    ).apply(i, ActionOnHitPower::new));
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;
    private final DamageCondition damageCondition;
    private final CooldownSettings cooldown;

    public ActionOnHitPower(BaseSettings settings, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, DamageCondition damageCondition, CooldownSettings cooldown) {
        super(settings);
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.damageCondition = damageCondition;
        this.cooldown = cooldown;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    public CooldownSettings getCooldown() {
        return this.cooldown;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public List<PowerComponent> createComponents() {
        return List.of(new CooldownComponent(this.getCooldown().cooldown()));
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        Entity source = event.getSource().getEntity(), target = event.getEntity();
        if (source == null) return;
        OriginDataHolder holder = OriginDataHolder.get(source);
        holder.streamActivePowers(ActionOnHitPower.class).forEach(power -> {
            if (power.getBiEntityCondition().test(source, target) && power.getDamageCondition().test(event.getSource(), event.getNewDamage()))
                holder.getComponentFor(power, CooldownComponent.class).ifPresent(c -> c.useIfReady(() -> power.getBiEntityAction().execute(source, target)));
        });
    }
}