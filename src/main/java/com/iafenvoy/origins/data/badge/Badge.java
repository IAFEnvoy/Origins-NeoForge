package com.iafenvoy.origins.data.badge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface Badge {
    Codec<Badge> CODEC = BadgeRegistries.BADGE.byNameCodec().dispatch("type", Badge::codec, x -> x);

    @NotNull
    MapCodec<? extends Badge> codec();

    void execute(@NotNull LivingEntity living, @NotNull Level level, @NotNull RegistryAccess access);
}
