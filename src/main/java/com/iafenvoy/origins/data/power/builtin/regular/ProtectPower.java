package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

/** Calls nearby matching mobs to attack an entity that hurt the holder or was hurt by the holder. */
@EventBusSubscriber
public final class ProtectPower extends Power {
    public static final MapCodec<ProtectPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("protector_condition").forGetter(ProtectPower::protectorCondition),
            Codec.DOUBLE.optionalFieldOf("radius", 16.0).forGetter(ProtectPower::radius)
    ).apply(instance, ProtectPower::new));
    private final EntityCondition protectorCondition;
    private final double radius;

    public ProtectPower(BaseSettings settings, EntityCondition protectorCondition, double radius) {
        super(settings);
        this.protectorCondition = protectorCondition;
        this.radius = Math.max(0, radius);
    }

    public EntityCondition protectorCondition() { return this.protectorCondition; }
    public double radius() { return this.radius; }

    @Override
    public @NotNull MapCodec<? extends Power> codec() { return CODEC; }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity attackerLiving) callProtectors(victim, attackerLiving);
        if (attacker instanceof LivingEntity holder && victim != holder) callProtectors(holder, victim);
    }

    private static void callProtectors(LivingEntity holder, LivingEntity enemy) {
        PowerHelper.get(holder).execute(ProtectPower.class, (data, power) -> {
            Level level = holder.level();
            AABB searchBox = holder.getBoundingBox().inflate(power.radius);
            for (Mob protector : level.getEntitiesOfClass(Mob.class, searchBox,
                    mob -> mob.isAlive() && mob != enemy && power.protectorCondition.test(mob))) {
                protector.setTarget(enemy);
            }
        });
    }
}
