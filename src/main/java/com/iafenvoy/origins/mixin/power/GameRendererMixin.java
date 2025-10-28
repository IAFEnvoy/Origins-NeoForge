package com.iafenvoy.origins.mixin.power;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void nightVisionPatch(LivingEntity livingEntity, float nanoTime, CallbackInfoReturnable<Float> cir) {
        if (!livingEntity.hasEffect(MobEffects.NIGHT_VISION)) cir.setReturnValue(0F);
    }
}
