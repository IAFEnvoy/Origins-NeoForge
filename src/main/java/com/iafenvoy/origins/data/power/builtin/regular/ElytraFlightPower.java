package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.client.ElytraTextureEvent;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ElytraFlightPower(boolean renderElytra, Optional<ResourceLocation> textureLocation) implements Power {
    public static final MapCodec<ElytraFlightPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("render_elytra", true).forGetter(ElytraFlightPower::renderElytra),
            ResourceLocation.CODEC.optionalFieldOf("texture_location").forGetter(ElytraFlightPower::textureLocation)
    ).apply(i, ElytraFlightPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void enableElytraFly(CanFlyWithoutElytraEvent event) {
        if (!EntityOriginAttachment.get(event.getEntity()).getPowers(RegularPowers.ELYTRA_FLIGHT, ElytraFlightPower.class).isEmpty())
            event.deny();
    }

    @SubscribeEvent
    public static void enableElytraRender(ElytraTextureEvent event) {
        for (ElytraFlightPower power : EntityOriginAttachment.get(event.getEntity()).getPowers(RegularPowers.ELYTRA_FLIGHT, ElytraFlightPower.class))
            if (power.renderElytra && power.textureLocation.isPresent()) {
                event.setTexture(power.textureLocation.get());
                break;
            }
    }
}
