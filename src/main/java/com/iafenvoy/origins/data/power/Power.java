package com.iafenvoy.origins.data.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface Power {
    Codec<Power> CODEC = PowerRegistries.POWER_TYPE.byNameCodec().dispatch("type", Power::codec, x -> x);

    @NotNull
    MapCodec<? extends Power> codec();

    default void grant(@NotNull Entity entity) {
    }

    default void revoke(@NotNull Entity entity) {
    }

    default void entityLoad(@NotNull Entity entity) {
    }

    default void entitySave(@NotNull Entity entity) {
    }

    default void tick(@NotNull Entity entity) {
    }
}
