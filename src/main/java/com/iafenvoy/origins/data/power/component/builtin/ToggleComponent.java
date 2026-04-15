package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ToggleComponent implements PowerComponent {
    public static final MapCodec<ToggleComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("active").forGetter(ToggleComponent::isActive)
    ).apply(i, ToggleComponent::new));
    private boolean active;

    public ToggleComponent() {
    }

    public ToggleComponent(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void toggle() {
        this.active ^= true;
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }
}
