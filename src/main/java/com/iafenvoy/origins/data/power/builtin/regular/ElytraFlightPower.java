package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.illusivesoulworks.caelus.api.CaelusApi;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ElytraFlightPower extends Power {
    public static final MapCodec<ElytraFlightPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("render_elytra", true).forGetter(ElytraFlightPower::shouldRenderElytra),
            ResourceLocation.CODEC.optionalFieldOf("texture_location").forGetter(ElytraFlightPower::getTextureLocation)
    ).apply(i, ElytraFlightPower::new));
    private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "flight_modifier");
    private static final Holder<Attribute> FLIGHT_ATTRIBUTE = CaelusApi.getInstance().getFallFlyingAttribute();
    private static final AttributeModifier FLIGHT_MODIFIER = new AttributeModifier(MODIFIER_ID, 1.0F, AttributeModifier.Operation.ADD_VALUE);
    private final boolean renderElytra;
    private final Optional<ResourceLocation> textureLocation;

    public ElytraFlightPower(BaseSettings settings, boolean renderElytra, Optional<ResourceLocation> textureLocation) {
        super(settings);
        this.renderElytra = renderElytra;
        this.textureLocation = textureLocation;
    }

    public boolean shouldRenderElytra() {
        return this.renderElytra;
    }

    public Optional<ResourceLocation> getTextureLocation() {
        return this.textureLocation;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        super.active(holder);
        if (holder.getEntity() instanceof LivingEntity living && living.getAttributes().hasAttribute(FLIGHT_ATTRIBUTE)) {
            AttributeInstance instance = living.getAttribute(FLIGHT_ATTRIBUTE);
            if (instance != null && !instance.hasModifier(MODIFIER_ID)) instance.addTransientModifier(FLIGHT_MODIFIER);
        }
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        super.inactive(holder);
        if (holder.getEntity() instanceof LivingEntity living && living.getAttributes().hasAttribute(FLIGHT_ATTRIBUTE)) {
            AttributeInstance instance = living.getAttribute(FLIGHT_ATTRIBUTE);
            if (instance != null && instance.hasModifier(MODIFIER_ID)) instance.removeModifier(MODIFIER_ID);
        }
    }
}
