package com.iafenvoy.origins.util;

import java.util.List;

/**
 * Applies {@link Modifier} lists to base values following the Apoli modifier application order.
 */
public final class ModifierUtil {
    private ModifierUtil() {}

    public static double applyModifiers(List<Modifier> modifiers, double baseValue) {
        double value = baseValue;
        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.ADD_BASE_EARLY) value += mod.value();

        double additiveBase = 0;
        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.MULTIPLY_BASE_ADDITIVE) additiveBase += mod.value();
        value *= 1 + additiveBase;

        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.MULTIPLY_BASE_MULTIPLICATIVE) value *= 1 + mod.value();

        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.ADD_BASE_LATE) value += mod.value();

        double additiveTotal = 0;
        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.MULTIPLY_TOTAL_ADDITIVE) additiveTotal += mod.value();
        value *= 1 + additiveTotal;

        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.MULTIPLY_TOTAL_MULTIPLICATIVE) value *= 1 + mod.value();

        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.SET_TOTAL) value = mod.value();
        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.MIN_TOTAL) value = Math.max(value, mod.value());
        for (Modifier mod : modifiers)
            if (mod.operation() == ModifierOperation.MAX_TOTAL) value = Math.min(value, mod.value());

        return value;
    }
}
