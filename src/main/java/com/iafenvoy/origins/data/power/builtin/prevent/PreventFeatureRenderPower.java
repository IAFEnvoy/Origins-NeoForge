package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventFeatureRenderPower extends Power {
    public static final MapCodec<PreventFeatureRenderPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("armor", true).forGetter(PreventFeatureRenderPower::shouldRenderArmor),
            Codec.BOOL.optionalFieldOf("cape", true).forGetter(PreventFeatureRenderPower::shouldRenderCape),
            Codec.BOOL.optionalFieldOf("elytra", true).forGetter(PreventFeatureRenderPower::shouldRenderElytra),
            Codec.BOOL.optionalFieldOf("held_item", true).forGetter(PreventFeatureRenderPower::shouldRenderHeldItem)
    ).apply(i, PreventFeatureRenderPower::new));
    private final boolean renderArmor, renderCape, renderElytra, renderHeldItem;

    public PreventFeatureRenderPower(BaseSettings settings, boolean renderArmor, boolean renderCape, boolean renderElytra, boolean renderHeldItem) {
        super(settings);
        this.renderArmor = renderArmor;
        this.renderCape = renderCape;
        this.renderElytra = renderElytra;
        this.renderHeldItem = renderHeldItem;
    }

    public boolean shouldRenderArmor() {
        return this.renderArmor;
    }

    public boolean shouldRenderCape() {
        return this.renderCape;
    }

    public boolean shouldRenderElytra() {
        return this.renderElytra;
    }

    public boolean shouldRenderHeldItem() {
        return this.renderHeldItem;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
