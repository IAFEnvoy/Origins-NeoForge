package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnTargetDeathPower extends HasCooldownPower {
    public static final MapCodec<ActionOnTargetDeathPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(ActionOnTargetDeathPower::getCooldown),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionOnTargetDeathPower::getBiEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionOnTargetDeathPower::getDamageCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnTargetDeathPower::getBiEntityCondition),
            Codec.BOOL.optionalFieldOf("includes_prime_adversary", true).forGetter(ActionOnTargetDeathPower::includesPrimeAdversary)
    ).apply(instance, ActionOnTargetDeathPower::new));
    private final BiEntityAction bientityAction;
    private final DamageCondition damageCondition;
    private final BiEntityCondition bientityCondition;
    private final boolean includesPrimeAdversary;

    public ActionOnTargetDeathPower(BaseSettings settings, CooldownSettings cooldown, BiEntityAction bientityAction,
                                    DamageCondition damageCondition, BiEntityCondition bientityCondition,
                                    boolean includesPrimeAdversary) {
        super(settings, cooldown);
        this.bientityAction = bientityAction;
        this.damageCondition = damageCondition;
        this.bientityCondition = bientityCondition;
        this.includesPrimeAdversary = includesPrimeAdversary;
    }

    public BiEntityAction getBiEntityAction() {
        return this.bientityAction;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.bientityCondition;
    }

    public boolean includesPrimeAdversary() {
        return this.includesPrimeAdversary;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        Entity actor = event.getSource().getEntity();
        if (actor == null) return;

        PowerHelper.get(actor).execute(ActionOnTargetDeathPower.class,
                power -> (power.includesPrimeAdversary || target.getKillCredit() != actor)
                        && power.damageCondition.test(event.getSource(), 1)
                        && power.bientityCondition.test(actor, target),
                (holder, power) -> power.getCooldownComponent(holder).useIfReady(() -> power.bientityAction.execute(actor, target)));
    }
}
