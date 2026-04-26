package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.common.KeySettings;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.ToggleComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TogglePower extends Power implements Toggleable {
    public static final MapCodec<TogglePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("active_by_default", true).forGetter(TogglePower::isActiveByDefault),
            Codec.BOOL.optionalFieldOf("retain_state", true).forGetter(TogglePower::isRetainState),
            KeySettings.CODEC.forGetter(TogglePower::getKey)
    ).apply(i, TogglePower::new));
    private final boolean activeByDefault;
    private final boolean retainState;
    private final KeySettings key;

    public TogglePower(BaseSettings settings, boolean activeByDefault, boolean retainState, KeySettings key) {
        super(settings);
        this.activeByDefault = activeByDefault;
        this.retainState = retainState;
        this.key = key;
    }

    public boolean isActiveByDefault() {
        return this.activeByDefault;
    }

    public boolean isRetainState() {
        return this.retainState;
    }

    public KeySettings getKey() {
        return this.key;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void createComponents(ComponentCollector collector) {
        collector.add(new ToggleComponent(this.activeByDefault));
    }

    @Override
    public void toggle(@NotNull OriginDataHolder holder, String key) {
        if (Objects.equals(this.key.key(), key))
            holder.getComponentFor(this, ToggleComponent.class).ifPresent(x -> {
                x.toggle();
                x.sendMessage(holder, key);
            });
    }

    @Override
    public boolean isActive(OriginDataHolder holder) {
        return holder.getComponentFor(this, ToggleComponent.class).map(ToggleComponent::isActive).orElse(false);
    }
}
