package com.iafenvoy.origins.data.power.helper;

import com.iafenvoy.origins.util.math.Modifier;

import java.util.List;

@FunctionalInterface
public interface ModifierPowerHelper {
    List<Modifier> getModifier();

    default int modify(int baseValue) {
        return Modifier.applyModifiers(this.getModifier(), baseValue);
    }

    default float modify(float baseValue) {
        return Modifier.applyModifiers(this.getModifier(), baseValue);
    }

    default double modify(double baseValue) {
        return Modifier.applyModifiers(this.getModifier(), baseValue);
    }
}
