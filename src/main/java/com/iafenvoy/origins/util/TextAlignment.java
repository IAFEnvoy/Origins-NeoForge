package com.iafenvoy.origins.util;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public enum TextAlignment implements StringRepresentable {
    NONE("none", (left, right, textWidth) -> null),
    LEFT("left", (left, right, textWidth) -> left - 1),
    RIGHT("right", (left, right, textWidth) -> right - textWidth + 1),
    CENTER("center", (left, right, textWidth) -> (left + right - textWidth) / 2);

    final String name;
    final PositionSupplier horizontalSupplier;

    TextAlignment(String name, PositionSupplier horizontalSupplier) {
        this.name = name;
        this.horizontalSupplier = horizontalSupplier;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public Optional<Integer> horizontal(int left, int right, int textWidth) {
        return Optional.ofNullable(this.horizontalSupplier.apply(left, right, textWidth));
    }

    @FunctionalInterface
    public interface PositionSupplier {
        Integer apply(int left, int right, int textWidth);
    }

}

