package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
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
public record FireImmunityPower(EntityCondition condition) implements Power {
    public static final MapCodec<FireImmunityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.optionalCodec("condition").forGetter(FireImmunityPower::condition)
    ).apply(i, FireImmunityPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void enableFireImmune(EntityFireImmuneEvent event) {
        Entity entity = event.getEntity();
        for (Power power : EntityOriginAttachment.get(entity).getPowers(RegularPowers.FIRE_IMMUNITY))
            if (power instanceof FireImmunityPower(EntityCondition condition) && condition.test(entity))
                event.allow();
    }
}
