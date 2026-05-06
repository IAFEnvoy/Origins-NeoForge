package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EndRespawningEntity;
import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyPlayerSpawnPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements ContainerListener, EndRespawningEntity {
    @Shadow
    private ResourceKey<Level> respawnDimension;
    @Shadow
    private BlockPos respawnPosition;
    @Shadow
    @Final
    public MinecraftServer server;
    @Shadow
    public ServerGamePacketListenerImpl connection;
    @Shadow
    private boolean respawnForced;
    @Shadow
    private float respawnAngle;

    @Shadow
    private static Optional<ServerPlayer.RespawnPosAngle> findRespawnAndUseSpawnBlock(ServerLevel world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive) {
        throw new AssertionError();
    }

    private ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @ModifyReturnValue(method = "getRespawnDimension", at = @At("RETURN"))
    private ResourceKey<Level> apoli$modifySpawnPointDimension(ResourceKey<Level> original) {
        if (!this.apoli$isEndRespawning() && (this.respawnPosition == null || this.apoli$hasObstructedOriginalSpawnPoint())) {
            return OriginDataHolder.get(this).streamActivePowers(ModifyPlayerSpawnPower.class)
                    .findFirst()
                    .map(ModifyPlayerSpawnPower::getDimension)
                    .orElse(original);
        } else return original;
    }

    @ModifyReturnValue(method = "getRespawnPosition", at = @At("RETURN"))
    private BlockPos apoli$modifySpawnPointPosition(BlockPos original) {

        if (this.apoli$isEndRespawning() || !OriginDataHolder.get(this).hasActivePower(ModifyPlayerSpawnPower.class)) {
            return original;
        } else if (original == null) {
            return this.apoli$findPowerSpawnPoint();
        } else if (this.apoli$hasObstructedOriginalSpawnPoint()) {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
            return this.apoli$findPowerSpawnPoint();
        } else {
            return original;
        }

    }

    @ModifyReturnValue(method = "isRespawnForced", at = @At("RETURN"))
    private boolean apoli$modifySpawnForced(boolean original) {
        return original || (!this.apoli$isEndRespawning() && (this.respawnPosition == null || this.apoli$hasObstructedOriginalSpawnPoint()) && OriginDataHolder.get(this).hasActivePower(ModifyPlayerSpawnPower.class));
    }

    @WrapOperation(method = "findRespawnPositionAndUseSpawnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"))
    private Optional<ServerPlayer.RespawnPosAngle> apoli$retryObstructedSpawnPointIfFailed(ServerLevel world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive, Operation<Optional<ServerPlayer.RespawnPosAngle>> original) {

        Optional<ServerPlayer.RespawnPosAngle> originalRespawnPos = original.call(world, pos, spawnAngle, spawnForced, alive);

        if (originalRespawnPos.isEmpty() && OriginDataHolder.get(this).hasActivePower(ModifyPlayerSpawnPower.class)) {
            return Optional
                    .ofNullable(DismountHelper.findSafeDismountLocation(this.getType(), world, pos, spawnForced))
                    .map(newPos -> ServerPlayer.RespawnPosAngle.of(newPos, pos));
        } else {
            return originalRespawnPos;
        }

    }

    @Unique
    private boolean apoli$hasObstructedOriginalSpawnPoint() {
        ServerLevel spawnPointWorld = this.server.getLevel(this.respawnDimension);
        return this.respawnPosition != null
                && spawnPointWorld != null
                && findRespawnAndUseSpawnBlock(spawnPointWorld, this.respawnPosition, this.respawnAngle, this.respawnForced, true).isEmpty();
    }

    @Unique
    private BlockPos apoli$findPowerSpawnPoint() {
        return OriginDataHolder.get(this).streamActivePowers(ModifyPlayerSpawnPower.class)
                .findFirst()
                .flatMap(x -> x.getSpawn(this))
                .map(Tuple::getB)
                .orElse(null);
    }

    @Unique
    private boolean apoli$isEndRespawning;

    @Override
    public void apoli$setEndRespawning(boolean endSpawn) {
        this.apoli$isEndRespawning = endSpawn;
    }

    @Override
    public boolean apoli$isEndRespawning() {
        return this.apoli$isEndRespawning;
    }

    @Override
    public boolean apoli$hasRealRespawnPoint() {
        return this.respawnPosition != null && !this.apoli$hasObstructedOriginalSpawnPoint();
    }

    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "()Lnet/minecraft/stats/ServerRecipeBook;"))
    private ServerRecipeBook apoli$cachePlayerToRecipeBook(ServerRecipeBook original) {
        if (original instanceof PowerCraftingObject pco) pco.origins$setPlayer(this);
        return original;
    }
}
