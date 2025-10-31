package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.common.IgnoreWaterEvent;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public enum IgnoreWaterPower implements Power {
    INSTANCE;
    public static final MapCodec<IgnoreWaterPower> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void ignoreWater(IgnoreWaterEvent event) {
        if (!OriginDataHolder.get(event.getEntity()).getPowers(RegularPowers.IGNORE_WATER, IgnoreWaterPower.class).isEmpty())
            event.allow();
    }
}
