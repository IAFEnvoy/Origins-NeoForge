package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.iafenvoy.origins.event.common.CanNaturalRegenEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Unique
    private Player origins$self() {
        return (Player) (Object) this;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkNaturalSpawn(boolean original) {
        return original && NeoForge.EVENT_BUS.post(new CanNaturalRegenEvent(this.origins$self())).getResult().allow();
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void handleElytra(CallbackInfoReturnable<Boolean> cir) {
        Player player = this.origins$self();
        if (!player.onGround() && !player.isFallFlying() && !player.isInWater()) {
            if (NeoForge.EVENT_BUS.post(new CanFlyWithoutElytraEvent(player)).getResult().allow()) {
                player.startFallFlying();
                cir.setReturnValue(true);
            }
        }
    }

    //Prevent player from damage when selection origins
    //TODO::Using events instead of mixin for this
    @ModifyExpressionValue(method = "isInvulnerableTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean origins$makePlayerInvulnerable(boolean original) {
        return original || !OriginDataHolder.get(this.origins$self()).hasAllOrigins();
    }
}
