package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

public record Modifier(double amount, ModifierOperation operation) {
    public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.fieldOf("amount").forGetter(Modifier::amount),
            ModifierOperation.CODEC.optionalFieldOf("operation", ModifierOperation.ADD_BASE_EARLY).forGetter(Modifier::operation)
    ).apply(i, Modifier::new));

    public double apply(double value) {
        return applyModifiers(List.of(this), value);
    }

    public int apply(int value) {
        return (int) applyModifiers(List.of(this), value);
    }

    public static double applyModifiers(List<Modifier> modifiers, double value) {
        Map<ModifierOperation, DoubleList> modifierMap = new EnumMap<>(ModifierOperation.class);
        modifiers.forEach(m -> modifierMap.computeIfAbsent(m.operation(), op -> new DoubleArrayList()).add(m.amount()));
        for (ModifierOperation operation : ModifierOperation.values())
            if (modifierMap.containsKey(operation))
                value = operation.getOperator().applyAsDouble(value, modifierMap.get(operation));
        return value;
    }

    public enum ModifierOperation implements StringRepresentable {
        ADD_BASE_EARLY(Double::sum, false),
        MULTIPLY_BASE_ADDITIVE((cur, val) -> cur * (1 + val), true),
        MULTIPLY_BASE_MULTIPLICATIVE((cur, val) -> cur * (1 + val), false),
        ADD_BASE_LATE(Double::sum, false),
        MULTIPLY_TOTAL_ADDITIVE((cur, val) -> cur * (1 + val), true),
        MULTIPLY_TOTAL_MULTIPLICATIVE((cur, val) -> cur * (1 + val), false),
        SET_TOTAL((cur, val) -> val, false),
        MIN_TOTAL(Math::min, false),
        MAX_TOTAL(Math::max, false);
        public static final Codec<ModifierOperation> CODEC = StringRepresentable.fromEnum(ModifierOperation::values);

        private final MultiDoubleBinaryOperator operator;

        ModifierOperation(DoubleBinaryOperator operator, boolean sum) {
            this.operator = sum ? (v, l) -> operator.applyAsDouble(v, l.doubleStream().sum()) : (v, l) -> l.doubleStream().collect(() -> v, operator::applyAsDouble, (v1, v2) -> {
            });
        }

        public MultiDoubleBinaryOperator getOperator() {
            return this.operator;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @FunctionalInterface
        public interface MultiDoubleBinaryOperator {
            double applyAsDouble(double value, DoubleList list);
        }
    }
}
