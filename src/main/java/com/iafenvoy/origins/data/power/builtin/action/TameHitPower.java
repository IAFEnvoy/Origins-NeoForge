package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

abstract class TameHitPower extends HasCooldownPower {
    protected final DamageCondition damageCondition;
    protected final BiEntityAction bientityAction;
    protected final BiEntityCondition bientityCondition;
    protected final BiEntityAction ownerBientityAction;
    protected final BiEntityCondition ownerBientityCondition;

    protected TameHitPower(BaseSettings settings, CooldownSettings cooldown, DamageCondition damageCondition, BiEntityAction bientityAction, BiEntityCondition bientityCondition, BiEntityAction ownerBientityAction, BiEntityCondition ownerBientityCondition) {
        super(settings, cooldown);
        this.damageCondition = damageCondition;
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.ownerBientityAction = ownerBientityAction;
        this.ownerBientityCondition = ownerBientityCondition;
    }

    protected static <T extends TameHitPower> void execute(T power, com.iafenvoy.origins.attachment.OriginDataHolder holder, Entity actor, Entity target, Entity ownerActor, Entity ownerTarget, DamageSource source, float amount) {
        if (power.damageCondition.test(source, amount) && power.bientityCondition.test(actor, target) && power.ownerBientityCondition.test(ownerActor, ownerTarget)) {
            power.getCooldownComponent(holder).useIfReady(() -> {
                power.bientityAction.execute(actor, target);
                power.ownerBientityAction.execute(ownerActor, ownerTarget);
            });
        }
    }
}
