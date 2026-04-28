package com.iafenvoy.origins.util.math;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.DoubleBinaryOperator;

public record Modifier(double value, ModifierOperation operation, Optional<ResourceLocation> resource,
                       Optional<Modifier> modifier) {
    public static final Codec<Modifier> CODEC = Codec.recursive(Modifier.class.getSimpleName(), codec -> RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.fieldOf("value").forGetter(Modifier::value),
            ModifierOperation.CODEC.optionalFieldOf("operation", ModifierOperation.ADD_BASE_EARLY).forGetter(Modifier::operation),
            ResourceLocation.CODEC.optionalFieldOf("resource").forGetter(Modifier::resource),
            codec.optionalFieldOf("modifier").forGetter(Modifier::modifier)
    ).apply(i, Modifier::new)));

    public double getValue(OriginDataHolder holder) {
        return this.resource.flatMap(x -> holder.getComponent(x, ResourceComponent.class)).map(ResourceComponent::getValue).map(Double.class::cast)
                .map(x -> this.modifier.map(m -> applyModifiers(holder, List.of(m), x)).orElse(x))
                .orElse(this.value);
    }

    public static int applyModifiers(OriginDataHolder holder, List<Modifier> modifiers, int value) {
        return (int) applyModifiers(holder, modifiers, (double) value);
    }

    public static float applyModifiers(OriginDataHolder holder, List<Modifier> modifiers, float value) {
        return (float) applyModifiers(holder, modifiers, (double) value);
    }

    public static double applyModifiers(OriginDataHolder holder, List<Modifier> modifiers, double value) {
        Map<ModifierOperation, DoubleList> modifierMap = new EnumMap<>(ModifierOperation.class);
        modifiers.forEach(m -> modifierMap.computeIfAbsent(m.operation(), op -> new DoubleArrayList()).add(m.getValue(holder)));
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
