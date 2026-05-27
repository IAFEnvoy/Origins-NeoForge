package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.HudRender;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public abstract class HasCooldownPower extends Power implements HudRenderable {
    private final CooldownSettings cooldown;

    protected HasCooldownPower(BaseSettings settings, CooldownSettings cooldown) {
        super(settings);
        this.cooldown = cooldown;
    }

    public CooldownSettings getCooldown() {
        return this.cooldown;
    }

    @Override
    public void createComponents(ComponentCollector collector) {
        super.createComponents(collector);
        collector.add(new CooldownComponent(this.cooldown.cooldown()));
    }

    @Override
    public Power getPowerForHudRender() {
        return this;
    }

    @Override
    public Optional<HudRender> getHudRenderData() {
        return this.cooldown.hudRender();
    }

    @Override
    public float getRenderPercentage(OriginDataHolder holder) {
        return HudRenderable.clampProgress(this.getCooldownComponent(holder).getValue(), 0, this.cooldown.cooldown());
    }

    @Override
    public boolean shouldRender(OriginDataHolder holder) {
        return this.getCooldownComponent(holder).getValue() > 0;
    }

    protected CooldownComponent getCooldownComponent(OriginDataHolder holder) {
        return holder.getComponentFor(this, CooldownComponent.class).orElse(new CooldownComponent(1));
    }

    public record CooldownSettings(int cooldown, Optional<HudRender> hudRender) {
        public static final MapCodec<CooldownSettings> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.optionalFieldOf("cooldown", 1).forGetter(CooldownSettings::cooldown),
                HudRender.CODEC.optionalFieldOf("hud_render").forGetter(CooldownSettings::hudRender)
        ).apply(i, CooldownSettings::new));
    }
}
