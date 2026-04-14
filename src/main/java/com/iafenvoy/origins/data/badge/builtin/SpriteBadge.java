package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record SpriteBadge(ResourceLocation sprite) implements Badge {
    public static final MapCodec<SpriteBadge> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(SpriteBadge::sprite)
    ).apply(i, SpriteBadge::new));

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull LivingEntity living, @NotNull Level level, @NotNull RegistryAccess access) {
    }

    @Override
    public ResourceLocation spriteId() {
        return this.sprite;
    }
}
