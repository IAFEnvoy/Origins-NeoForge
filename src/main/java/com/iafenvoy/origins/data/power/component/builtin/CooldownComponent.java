package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CooldownComponent implements PowerComponent {
    public static final MapCodec<CooldownComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("defaultValue").forGetter(CooldownComponent::getDefaultValue),
            Codec.INT.fieldOf("value").forGetter(CooldownComponent::getValue)
    ).apply(i, CooldownComponent::new));
    private final int defaultValue;
    private int value;

    public CooldownComponent(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public CooldownComponent(int defaultValue, int value) {
        this(defaultValue);
        this.value = value;
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public int getValue() {
        return this.value;
    }

    public void startCooldown() {
        this.value = this.defaultValue;
    }

    public boolean canUse() {
        return this.value <= 0;
    }

    @Override
    public void tick(OriginDataHolder holder, ResourceLocation id) {
        if (!this.canUse()) this.value--;
    }
}
