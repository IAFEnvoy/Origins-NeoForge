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
            Codec.INT.fieldOf("value").forGetter(CooldownComponent::value)
    ).apply(i, CooldownComponent::new));
    private int value;

    public CooldownComponent(int value) {
        this.value = value;
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }

    public int value() {
        return this.value;
    }

    @Override
    public void tick(OriginDataHolder holder, ResourceLocation id) {
        if (this.value > 0) this.value--;
    }
}
