package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Comparison(CompareOperation comparison, double compareTo) {
    public static final MapCodec<Comparison> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CompareOperation.CODEC.fieldOf("comparison").forGetter(Comparison::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(Comparison::compareTo)
    ).apply(i, Comparison::new));

    public static MapCodec<Comparison> optionalCodec(CompareOperation operation, double compareTo) {
        return RecordCodecBuilder.mapCodec(i -> i.group(
                CompareOperation.CODEC.optionalFieldOf("comparison", operation).forGetter(Comparison::comparison),
                Codec.DOUBLE.optionalFieldOf("compare_to", compareTo).forGetter(Comparison::compareTo)
        ).apply(i, Comparison::new));
    }

    public boolean compare(double current) {
        return this.comparison.compare(current, this.compareTo);
    }

    public boolean compare(int current) {
        return this.comparison.compare(current, this.compareTo);
    }

    public enum CompareOperation implements StringRepresentable {
        LESS_THAN("<", (a, b) -> a < b),
        LESS_THAN_OR_EQUAL("<=", (a, b) -> a <= b),
        GREATER_THAN(">", (a, b) -> a > b),
        GREATER_THAN_OR_EQUAL(">=", (a, b) -> a >= b),
        EQUAL("==", Objects::equals),
        NOT_EQUAL("!=", (a, b) -> !Objects.equals(a, b));
        public static final Codec<CompareOperation> CODEC = StringRepresentable.fromValues(CompareOperation::values);
        private final String key;
        private final Comparator comparator;

        CompareOperation(String key, Comparator comparator) {
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
}
