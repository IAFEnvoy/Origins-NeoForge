package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
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
public class ActionOnHitPower extends HasCooldownPower {
    public static final MapCodec<ActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(ActionOnHitPower::getCooldown),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionOnHitPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnHitPower::getBiEntityCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionOnHitPower::getDamageCondition)
    ).apply(i, ActionOnHitPower::new));
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;
    private final DamageCondition damageCondition;

    public ActionOnHitPower(BaseSettings settings, CooldownSettings cooldown, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, DamageCondition damageCondition) {
        super(settings, cooldown);
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.damageCondition = damageCondition;
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

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        Entity source = event.getSource().getEntity(), target = event.getEntity();
        if (source == null) return;
        OriginDataHolder holder = OriginDataHolder.get(source);
        holder.getHelper().execute(ActionOnHitPower.class,
                p -> p.biEntityCondition.test(source, target) && p.damageCondition.test(event.getSource(), event.getNewDamage()),
                p -> p.getCooldownComponent(holder).useIfReady(() -> p.biEntityAction.execute(source, target)));
    }
}