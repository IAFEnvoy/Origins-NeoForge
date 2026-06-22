package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.data.badge.Badge;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record KeybindBadge(Identifier sprite, String text, String key) implements Badge {
    public static final MapCodec<KeybindBadge> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("sprite").forGetter(KeybindBadge::sprite),
            Codec.STRING.optionalFieldOf("text", "").forGetter(KeybindBadge::text),
            Codec.STRING.optionalFieldOf("key", Constants.PRIMARY_ACTIVE_KEY).forGetter(KeybindBadge::key)
    ).apply(i, KeybindBadge::new));

    public KeybindBadge(Identifier sprite, String text) {
        this(sprite, text, Constants.PRIMARY_ACTIVE_KEY);
    }

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }
}
