package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.event.client.NightVisionStrengthEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@OnlyIn(Dist.CLIENT)
@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Unique
    @NotNull
    private static Optional<Float> NIGHT_VISION_STRENGTH = Optional.empty();

    @ModifyExpressionValue(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0))
    private static boolean handleNightVisionStrength1(boolean original, @Local LivingEntity living) {
        NIGHT_VISION_STRENGTH = NeoForge.EVENT_BUS.post(new NightVisionStrengthEvent(living)).getStrength();
        return original || NIGHT_VISION_STRENGTH.isPresent();
    }

    @ModifyExpressionValue(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F"))
    private static float handleNightVisionStrength2(float original) {
        return NIGHT_VISION_STRENGTH.orElse(original);
    }
}
