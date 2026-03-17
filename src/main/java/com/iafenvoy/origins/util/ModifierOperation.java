package com.iafenvoy.origins.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Operations for value modification, applied in a defined order by {@link ModifierUtil}.
 * Mirrors the Apoli modifier operation system.
 */
public enum ModifierOperation implements StringRepresentable {
    ADD_BASE_EARLY,
    MULTIPLY_BASE_ADDITIVE,
    MULTIPLY_BASE_MULTIPLICATIVE,
    ADD_BASE_LATE,
    MULTIPLY_TOTAL_ADDITIVE,
    MULTIPLY_TOTAL_MULTIPLICATIVE,
    SET_TOTAL,
    MIN_TOTAL,
    MAX_TOTAL;

    public static final Codec<ModifierOperation> CODEC = StringRepresentable.fromEnum(ModifierOperation::values);

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
