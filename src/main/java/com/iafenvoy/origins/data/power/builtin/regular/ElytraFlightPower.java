package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.client.ElytraTextureEvent;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EventBusSubscriber
public class ElytraFlightPower extends Power {
    public static final MapCodec<ElytraFlightPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("render_elytra", true).forGetter(ElytraFlightPower::isRenderElytra),
            ResourceLocation.CODEC.optionalFieldOf("texture_location").forGetter(ElytraFlightPower::getTextureLocation)
    ).apply(i, ElytraFlightPower::new));
    private final boolean renderElytra;
    private final Optional<ResourceLocation> textureLocation;
    private static final ResourceLocation ELYTRA_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");

    public ElytraFlightPower(BaseSettings settings, boolean renderElytra, Optional<ResourceLocation> textureLocation) {
        super(settings);
        this.renderElytra = renderElytra;
        this.textureLocation = textureLocation;
    }

    public boolean isRenderElytra() {
        return this.renderElytra;
    }

    public Optional<ResourceLocation> getTextureLocation() {
        return this.textureLocation;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void enableElytraFly(CanFlyWithoutElytraEvent event) {
        if (!OriginDataHolder.get(event.getEntity()).getPowers(RegularPowers.ELYTRA_FLIGHT, ElytraFlightPower.class).isEmpty())
            event.allow();
    }

    @SubscribeEvent
    public static void enableElytraRender(ElytraTextureEvent event) {
        for (ElytraFlightPower power : OriginDataHolder.get(event.getEntity()).getPowers(RegularPowers.ELYTRA_FLIGHT, ElytraFlightPower.class))
            if (power.isRenderElytra()) {
                event.setTexture(power.getTextureLocation().orElse(ELYTRA_TEXTURE));
                break;
            }
    }
}
