package com.iafenvoy.origins.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Explosion;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Shared destruction type enum used by both entity and block ExplodeActions.
 */
public enum DestructionType implements StringRepresentable {
    NONE(Explosion.BlockInteraction.KEEP),
    BREAK(Explosion.BlockInteraction.DESTROY_WITH_DECAY),
    DESTROY(Explosion.BlockInteraction.DESTROY);

    public static final Codec<DestructionType> CODEC = StringRepresentable.fromEnum(DestructionType::values);
    private final Explosion.BlockInteraction interaction;

    DestructionType(Explosion.BlockInteraction interaction) {
        this.interaction = interaction;
    }

    public Explosion.BlockInteraction getInteraction() {
        return this.interaction;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
