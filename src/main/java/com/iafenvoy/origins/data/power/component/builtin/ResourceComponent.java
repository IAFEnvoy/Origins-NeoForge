package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntBinaryOperator;

public class ResourceComponent extends PowerComponent {
    public static final MapCodec<ResourceComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("value").forGetter(ResourceComponent::getValue)
    ).apply(i, ResourceComponent::new));
    private int value;

    public ResourceComponent(int value) {
        this.value = value;
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }

    public int getValue() {
        return this.value;
    }

    public void updateResource(Int2IntFunction operation) {
        this.value = operation.applyAsInt(this.value);
        this.markDirty();
    }

    public void updateResource(IntBinaryOperator operation, int value) {
        this.value = operation.applyAsInt(this.value, value);
        this.markDirty();
    }
}
