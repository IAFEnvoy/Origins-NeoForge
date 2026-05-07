package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EndRespawningEntity;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;respawn(Lnet/minecraft/server/level/ServerPlayer;ZLnet/minecraft/world/entity/Entity$RemovalReason;)Lnet/minecraft/server/level/ServerPlayer;", ordinal = 0))
    private void saveEndRespawnStatus(ServerboundClientCommandPacket packet, CallbackInfo ci) {
        ((EndRespawningEntity) this.player).origins$setEndRespawning(true);
    }

    @Inject(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ChangeDimensionTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceKey;)V"))
    private void undoEndRespawnStatus(ServerboundClientCommandPacket packet, CallbackInfo ci) {
        ((EndRespawningEntity) this.player).origins$setEndRespawning(false);
    }
}
