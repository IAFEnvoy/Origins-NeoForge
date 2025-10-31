package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiPredicate;

public enum Comparison implements StringRepresentable {
    //a=current value, b=given value
    LESS_THAN("<", (a, b) -> a < b),
    LESS_THAN_OR_EQUAL("<=", (a, b) -> a <= b),
    GREATER_THAN(">", (a, b) -> a > b),
    GREATER_THAN_OR_EQUAL(">=", (a, b) -> a >= b),
    EQUAL("==", Objects::equals),
    NOT_EQUAL("!=", (a, b) -> !Objects.equals(a, b));
    public static final Codec<Comparison> CODEC = StringRepresentable.fromValues(Comparison::values);
    private final String key;
    private final BiPredicate<Double, Double> comparator;

    Comparison(String key, BiPredicate<Double, Double> comparator) {
        this.key = key;
        this.comparator = comparator;
    }

    public boolean test(double current, double given) {
        return this.comparator.test(current, given);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.key;
    }
}
