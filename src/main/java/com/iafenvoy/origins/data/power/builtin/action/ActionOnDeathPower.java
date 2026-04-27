package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnDeathPower extends Power {
    public static final MapCodec<ActionOnDeathPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ActionOnDeathPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnDeathPower::getBiEntityCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionOnDeathPower::getDamageCondition)
    ).apply(i, ActionOnDeathPower::new));
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;
    private final DamageCondition damageCondition;

    public ActionOnDeathPower(BaseSettings settings, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, DamageCondition damageCondition) {
        super(settings);
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
    public static void onDeath(LivingDeathEvent event) {
        Entity actor = event.getSource().getEntity(), target = event.getEntity();
        if (actor == null) return;
        OriginDataHolder.get(target).streamActivePowers(ActionOnDeathPower.class).forEach(power -> {
            if (power.biEntityCondition.test(target, actor) && power.damageCondition.test(event.getSource(), 1))
                power.biEntityAction.execute(target, actor);
        });
    }
}
