package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data._common.HudRender;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;

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

    protected CooldownComponent getCooldownComponent(OriginDataHolder holder) {
        return holder.getComponentFor(this, CooldownComponent.class).orElse(new CooldownComponent(1));
    }
}
