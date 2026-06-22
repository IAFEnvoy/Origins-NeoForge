package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCameraSubmersionPower;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class FogRendererMixin {
    @ModifyReturnValue(method = "getFluidInCamera", at = @At("RETURN"))
    private FogType origins$modifyCameraSubmersion(FogType original) {
        Camera camera = (Camera) (Object) this;
        return ModifyCameraSubmersionPower.tryReplace(camera.entity(), original).orElse(original);
    }

    @Mixin(FogRenderer.class)
    public static abstract class NightVisionFogMixin {
        @ModifyExpressionValue(
                method = "computeFogColor",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0)
        )
        private boolean origins$applyNightVisionPower(boolean original, @Local LivingEntity living) {
            return original || OriginDataHolder.get(living).hasActivePower(NightVisionPower.class);
        }
    }
}
