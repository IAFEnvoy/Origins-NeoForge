package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public enum UsingEffectiveToolCondition implements EntityCondition {
    INSTANCE;
    public static final MapCodec<UsingEffectiveToolCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    //FIXME::Correct logic?
    @Override
    public boolean test(@NotNull Entity entity) {
        if (!(entity instanceof Player player)) return false;
        BlockState state;
        if (player instanceof ServerPlayer serverPlayer) {
            ServerPlayerGameMode gameMode = serverPlayer.gameMode;
            if (!gameMode.isDestroyingBlock) return false;
            state = player.level().getBlockState(gameMode.destroyPos);
        } else if (player instanceof LocalPlayer localPlayer) {
            MultiPlayerGameMode gameMode = localPlayer.minecraft.gameMode;
            if (gameMode == null || !gameMode.isDestroying) return false;
            state = player.level().getBlockState(gameMode.destroyBlockPos);
        } else return false;
        return player.hasCorrectToolForDrops(state, player.level(), player.blockPosition());
    }
}
