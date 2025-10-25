package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.event.CheckNaturalRegenEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkNaturalSpawn(boolean original, @Local(argsOnly = true) Player player) {
        return original && !NeoForge.EVENT_BUS.post(new CheckNaturalRegenEvent(player)).isCanceled();
    }
}
