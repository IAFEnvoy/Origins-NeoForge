package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.common.CanNaturalRegenEvent;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public enum DisableRegenPower implements Power {
    INSTANCE;
    public static final MapCodec<DisableRegenPower> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void disableNaturalRegen(CanNaturalRegenEvent event) {
        if (!EntityOriginAttachment.get(event.getEntity()).getPowers(RegularPowers.DISABLE_REGEN, DisableRegenPower.class).isEmpty())
            event.deny();
    }
}
