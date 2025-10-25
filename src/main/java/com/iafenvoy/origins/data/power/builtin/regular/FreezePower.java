package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.common.EntityFrozenEvent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EventBusSubscriber
public record FreezePower(Optional<EntityCondition> condition) implements Power {
    public static final MapCodec<FreezePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.optionalFieldOf("condition").forGetter(FreezePower::condition)
    ).apply(i, FreezePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void enableFrozen(EntityFrozenEvent event) {
        Entity entity = event.getEntity();
        for (Power power : EntityOriginAttachment.get(entity).getPowers(RegularPowers.FREEZE))
            if (power instanceof FreezePower(
                    Optional<EntityCondition> condition
            ) && condition.map(x -> x.test(entity)).orElse(true))
                event.allow();
    }
}
