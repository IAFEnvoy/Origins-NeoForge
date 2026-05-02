package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record KeybindBadge(ResourceLocation sprite, String text) implements Badge {
    public static final MapCodec<KeybindBadge> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(KeybindBadge::sprite),
            Codec.STRING.optionalFieldOf("text", "").forGetter(KeybindBadge::text)
    ).apply(i, KeybindBadge::new));

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation spriteId() {
        return this.sprite;
    }
}
