package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.event.CheckNaturalRegenEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @Unique
    private Player origins$self() {
        return (Player) (Object) this;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkNaturalSpawn(boolean original) {
        return original && !NeoForge.EVENT_BUS.post(new CheckNaturalRegenEvent(this.origins$self())).isCanceled();
    }
}
