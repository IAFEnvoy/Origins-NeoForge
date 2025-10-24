package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public enum CreativeFlightPower implements Power {
    INSTANCE;
    public static final MapCodec<CreativeFlightPower> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void grant(@NotNull Entity entity) {
        if (entity instanceof Player player) player.getAbilities().mayfly = true;
    }

    @Override
    public void revoke(@NotNull Entity entity) {
        if (entity instanceof Player player) player.getAbilities().mayfly = false;
    }
}
