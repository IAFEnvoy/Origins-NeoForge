package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.common.HudRender;
import com.iafenvoy.origins.data.power.HudRenderable;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.iafenvoy.origins.util.codec.OptionalCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;

public class ResourcePower extends Power implements HudRenderable {
    public static final MapCodec<ResourcePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.fieldOf("min").forGetter(ResourcePower::getMin),
            Codec.INT.fieldOf("max").forGetter(ResourcePower::getMax),
            HudRender.CODEC.optionalFieldOf("hud_render").forGetter(ResourcePower::getHudRender),
            OptionalCodecs.integer("start_value").forGetter(ResourcePower::getStartValue),
            EntityAction.optionalCodec("min_action").forGetter(ResourcePower::getMinAction),
            EntityAction.optionalCodec("max_action").forGetter(ResourcePower::getMaxAction)
    ).apply(i, ResourcePower::new));
    private final int min;
    private final int max;
    private final Optional<HudRender> hudRender;
    private final OptionalInt startValue;
    private final EntityAction minAction;
    private final EntityAction maxAction;

    public ResourcePower(BaseSettings settings, int min, int max, Optional<HudRender> hudRender, OptionalInt startValue, EntityAction minAction, EntityAction maxAction) {
        super(settings);
        this.min = min;
        this.max = max;
        this.hudRender = hudRender;
        this.startValue = startValue;
        this.minAction = minAction;
        this.maxAction = maxAction;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public Optional<HudRender> getHudRender() {
        return this.hudRender;
    }

    public OptionalInt getStartValue() {
        return this.startValue;
    }

    public EntityAction getMinAction() {
        return this.minAction;
    }

    public EntityAction getMaxAction() {
        return this.maxAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void createComponents(ComponentCollector collector) {
        collector.add(new ResourceComponent(this.getStartValue().orElse(this.getMin())));
    }

    @Override
    public Power getPowerForHudRender() {
        return this;
    }

    @Override
    public Optional<HudRender> getHudRenderData() {
        return this.hudRender;
    }

    @Override
    public float getRenderPercentage(OriginDataHolder holder) {
        return HudRenderable.clampProgress(holder.getComponentFor(this, ResourceComponent.class).map(ResourceComponent::getValue).orElse(this.min), this.min, this.max);
    }
}
