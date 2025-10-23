package com.iafenvoy.origins.data.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface Power {
    Codec<Power> CODEC = PowerRegistries.POWER_TYPE.byNameCodec().dispatch("type", Power::codec, x -> x);

    MapCodec<? extends Power> codec();

    void execute(@NotNull LivingEntity living, @NotNull Level level, @NotNull RegistryAccess access);
}
