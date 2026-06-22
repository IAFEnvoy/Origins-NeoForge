package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EndRespawningEntity;
import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyPlayerSpawnPower;
import com.iafenvoy.origins.data.power.builtin.regular.DisableRegenPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ContainerListener, EndRespawningEntity {
    @Shadow
    @Nullable
    private ServerPlayer.RespawnConfig respawnConfig;

    @Shadow
    public abstract ServerRecipeBook getRecipeBook();

    @Shadow
    private static Optional<ServerPlayer.RespawnPosAngle> findRespawnAndUseSpawnBlock(
            ServerLevel level, ServerPlayer.RespawnConfig respawnConfig, boolean consumeSpawnBlock
    ) {
        throw new AssertionError();
    }

    private ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @ModifyExpressionValue(method = "tickRegeneration", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
    private boolean origins$checkNaturalRegeneration(boolean original) {
        return original && !OriginDataHolder.get(this).hasActivePower(DisableRegenPower.class);
    }

    @ModifyReturnValue(method = "getRespawnConfig", at = @At("RETURN"))
    private ServerPlayer.RespawnConfig origins$modifyRespawnConfig(@Nullable ServerPlayer.RespawnConfig original) {
        if (this.origins$isEndRespawning() || original != null) return original;
        return OriginDataHolder.get(this).streamActivePowers(ModifyPlayerSpawnPower.class)
                .findFirst()
                .flatMap(power -> power.getSpawn(this))
                .map(spawn -> new ServerPlayer.RespawnConfig(
                        LevelData.RespawnData.of(spawn.getA().dimension(), spawn.getB(), this.getYRot(), this.getXRot()),
                        true
                ))
                .orElse(null);
    }

    @ModifyReturnValue(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"))
    private TeleportTransition origins$usePowerSpawnWhenRespawnBlockIsMissing(
            TeleportTransition original,
            boolean consumeSpawnBlock,
            TeleportTransition.PostTeleportTransition postTeleportTransition
    ) {
        if (!original.missingRespawnBlock() || this.origins$isEndRespawning()) return original;
        return OriginDataHolder.get(this).streamActivePowers(ModifyPlayerSpawnPower.class)
                .findFirst()
                .flatMap(power -> power.getSpawn(this))
                .map(spawn -> new TeleportTransition(
                        spawn.getA(), Vec3.atBottomCenterOf(spawn.getB()), Vec3.ZERO,
                        this.getYRot(), this.getXRot(), postTeleportTransition
                ))
                .orElse(original);
    }

    @Unique
    private boolean origins$isEndRespawning;

    @Override
    public void origins$setEndRespawning(boolean endSpawn) {
        this.origins$isEndRespawning = endSpawn;
    }

    @Override
    public boolean origins$isEndRespawning() {
        return this.origins$isEndRespawning;
    }

    @Override
    public boolean origins$hasRealRespawnPoint() {
        if (this.respawnConfig == null) return false;
        ServerLevel respawnLevel = this.level().getServer().getLevel(
                ServerPlayer.RespawnConfig.getDimensionOrDefault(this.respawnConfig)
        );
        return respawnLevel != null && findRespawnAndUseSpawnBlock(respawnLevel, this.respawnConfig, false).isPresent();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void origins$cachePlayerToRecipeBook(CallbackInfo ci) {
        if (this.getRecipeBook() instanceof PowerCraftingObject pco) pco.origins$setPlayer(this);
    }
}
