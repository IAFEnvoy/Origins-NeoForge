package com.iafenvoy.origins.data.badge;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EmptyBadge implements Badge {
    public static final MapCodec<EmptyBadge> CODEC = MapCodec.unit(EmptyBadge::new);

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull LivingEntity living, @NotNull Level level, @NotNull RegistryAccess access) {

    }

    @Override
    public ResourceLocation spriteId() {
        return ResourceLocation.withDefaultNamespace("missingno");
    }
}
