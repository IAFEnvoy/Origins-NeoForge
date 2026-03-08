package com.iafenvoy.origins.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A data-driven modifier for numerical values, used by Modify* power types.
 * Supports both singular ("modifier") and plural ("modifiers") JSON forms via {@link ListConfiguration}.
 */
public record Modifier(double value, ModifierOperation operation) {
    public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.fieldOf("value").forGetter(Modifier::value),
            ModifierOperation.CODEC.optionalFieldOf("operation", ModifierOperation.ADD_BASE_EARLY).forGetter(Modifier::operation)
    ).apply(i, Modifier::new));
}
