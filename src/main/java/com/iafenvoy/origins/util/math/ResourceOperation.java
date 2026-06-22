package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.IntBinaryOperator;

public enum ResourceOperation implements StringRepresentable {
    ADD(Integer::sum),
    SET((cur, val) -> val);
    public static final Codec<ResourceOperation> CODEC = StringRepresentable.fromValues(ResourceOperation::values);
    private final IntBinaryOperator operator;

    ResourceOperation(IntBinaryOperator operator) {
        this.operator = operator;
    }

    public IntBinaryOperator getOperator() {
        return this.operator;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
