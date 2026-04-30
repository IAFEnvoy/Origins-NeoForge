package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EventBusSubscriber
public class PreventDeathPower extends Power {
    public static final MapCodec<PreventDeathPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            DamageCondition.optionalCodec("damage_condition").forGetter(PreventDeathPower::getDamageCondition),
            EntityAction.optionalCodec("entity_action").forGetter(PreventDeathPower::getEntityAction)
    ).apply(i, PreventDeathPower::new));
    private final DamageCondition damageCondition;
    private final EntityAction entityAction;

    protected PreventDeathPower(BaseSettings settings, DamageCondition damageCondition, EntityAction entityAction) {
        super(settings);
        this.damageCondition = damageCondition;
        this.entityAction = entityAction;
    }

    public DamageCondition getDamageCondition() {
        return this.damageCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static boolean tryPreventDeath(Entity entity, DamageSource source, float amount) {
        Optional<PreventDeathPower> first = OriginDataHolder.get(entity).streamActivePowers(PreventDeathPower.class)
                .filter(x -> x.damageCondition.test(source, amount))
                .findFirst();
        first.ifPresent(x -> {
            if (entity instanceof LivingEntity living) living.setHealth(1.0F);
            x.entityAction.execute(entity);
        });
        return first.isPresent();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventLivingDeath(LivingDeathEvent event) {
        //TODO::damage
        if (PreventDeathPower.tryPreventDeath(event.getEntity(), event.getSource(), 1)) {
            event.getEntity().setHealth(1.0F);
            event.setCanceled(true);
        }
    }
}
