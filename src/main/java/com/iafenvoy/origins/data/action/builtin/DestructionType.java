package com.iafenvoy.origins.data.action.builtin;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum DestructionType implements StringRepresentable {
    NONE(Level.ExplosionInteraction.NONE),
    BREAK(Level.ExplosionInteraction.BLOCK),
    DESTROY(Level.ExplosionInteraction.TNT);

    public static final Codec<DestructionType> CODEC = StringRepresentable.fromEnum(DestructionType::values);
    private final Level.ExplosionInteraction interaction;

    DestructionType(Level.ExplosionInteraction interaction) {
        this.interaction = interaction;
    }

    public Level.ExplosionInteraction getInteraction() {
        return this.interaction;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
