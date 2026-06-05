package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ResourcePower;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntBinaryOperator;

public class ResourceComponent extends PowerComponent {
    public static final MapCodec<ResourceComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("value").forGetter(ResourceComponent::getValue),
            //FIXME::Give default value for capability with old versions
            Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(ResourceComponent::getMin),
            Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(ResourceComponent::getMax)
    ).apply(i, ResourceComponent::new));
    private int value, min, max;
    private boolean checkCallback = false;

    public ResourceComponent(int value, int min, int max) {
        this.value = Math.clamp(value, min, max);
        this.min = min;
        this.max = max;
    }

    public int getValue() {
        return this.value;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }

    public void setValue(int value) {
        this.value = Math.clamp(value, this.min, this.max);
        this.markDirty();
    }

    public void updateResource(Int2IntFunction operation) {
        this.value = Math.clamp(operation.applyAsInt(this.value), this.min, this.max);
        this.markDirty();
    }

    public void updateResource(IntBinaryOperator operation, int value) {
        this.value = Math.clamp(operation.applyAsInt(this.value, value), this.min, this.max);
        this.markDirty();
    }

    @Override
    public void tick(OriginDataHolder holder, PowerHolder parent) {
        if (this.checkCallback && parent.power() instanceof ResourcePower power) {
            this.checkCallback = false;
            this.min = power.getMinValue();
            this.max = power.getMaxValue();
            if (this.value == this.min) power.getMinAction().execute(holder.getEntity());
            if (this.value == this.max) power.getMaxAction().execute(holder.getEntity());
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.checkCallback = true;
    }
}
