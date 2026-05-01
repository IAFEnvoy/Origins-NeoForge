package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.util.math.Modifier;

import java.util.List;

@FunctionalInterface
public interface ModifierPowerHelper {
    List<Modifier> getModifier();

    default int modify(OriginDataHolder holder, int baseValue) {
        return Modifier.applyModifiers(holder, this.getModifier(), baseValue);
    }

    default float modify(OriginDataHolder holder, float baseValue) {
        return Modifier.applyModifiers(holder, this.getModifier(), baseValue);
    }

    default double modify(OriginDataHolder holder, double baseValue) {
        return Modifier.applyModifiers(holder, this.getModifier(), baseValue);
    }
}
