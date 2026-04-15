package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.common.EntityFireImmuneEvent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class FireImmunityPower extends Power {
    public static final MapCodec<FireImmunityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("condition").forGetter(FireImmunityPower::getCondition)
    ).apply(i, FireImmunityPower::new));
    private final EntityCondition condition;

    public FireImmunityPower(BaseSettings settings, EntityCondition condition) {
        super(settings);
        this.condition = condition;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void enableFireImmune(EntityFireImmuneEvent event) {
        Entity entity = event.getEntity();
        for (FireImmunityPower power : OriginDataHolder.get(entity).getPowers(RegularPowers.FIRE_IMMUNITY, FireImmunityPower.class))
            if (power.condition.test(entity))
                event.allow();
    }
}
