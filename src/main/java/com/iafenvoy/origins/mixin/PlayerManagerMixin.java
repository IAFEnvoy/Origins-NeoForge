package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EndRespawningEntity;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    @WrapWithCondition(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;copyRespawnPosition(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private boolean apoli$preventEndExitSpawnpointResetting(ServerPlayer newPlayer, ServerPlayer oldPlayer) {
        return ((EndRespawningEntity) oldPlayer).apoli$hasRealRespawnPoint();//FIXME::To event
    }
}
