package com.iafenvoy.origins.data.badge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface Badge {
    Codec<Badge> CODEC = BadgeRegistries.BADGE.byNameCodec().dispatch("type", Badge::codec, x -> x);

    MapCodec<? extends Badge> codec();

    void execute(LivingEntity living, Level level, RegistryAccess access);
}
