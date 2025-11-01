package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum Comparison implements StringRepresentable {
    LESS_THAN("<", (a, b) -> a < b),
    LESS_THAN_OR_EQUAL("<=", (a, b) -> a <= b),
    GREATER_THAN(">", (a, b) -> a > b),
    GREATER_THAN_OR_EQUAL(">=", (a, b) -> a >= b),
    EQUAL("==", Objects::equals),
    NOT_EQUAL("!=", (a, b) -> !Objects.equals(a, b));
    public static final Codec<Comparison> CODEC = StringRepresentable.fromValues(Comparison::values);
    private final String key;
    private final Comparator comparator;

    Comparison(String key, Comparator comparator) {
        this.key = key;
        this.comparator = comparator;
    }

    public boolean compare(double current, double given) {
        return this.comparator.compare(current, given);
    }

    public boolean compare(double current, int given) {
        return this.comparator.compare(current, given);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.key;
    }

    @FunctionalInterface
    private interface Comparator {
        boolean compare(double a, double b);
    }
}
