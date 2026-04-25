package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
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

@EventBusSubscriber
public class FreezePower extends Power {
    public static final MapCodec<FreezePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("condition").forGetter(FreezePower::getCondition)
    ).apply(i, FreezePower::new));
    private final EntityCondition condition;

    public FreezePower(BaseSettings settings, EntityCondition condition) {
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
    public static void enableFrozen(EntityFrozenEvent event) {
        Entity entity = event.getEntity();
        for (FreezePower power : OriginDataHolder.get(entity).getPowers(RegularPowers.FREEZE, FreezePower.class))
            if (power.getCondition().test(entity))
                event.allow();
    }
}
