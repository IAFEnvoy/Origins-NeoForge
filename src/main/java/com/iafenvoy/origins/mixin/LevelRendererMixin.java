package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.iafenvoy.origins.render.LevelRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    public abstract void allChanged();

    @ModifyVariable(method = "renderLevel", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private boolean origins$skipSkyForPhasingBlindness(boolean shouldRenderSky) {
        if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living
                && PhasingPower.hasRenderMethod(living, PhasingPower.PhasingRenderType.BLINDNESS)
                && PhasingPower.getInWallBlockState(living) != null) {
            return false;
        }
        return shouldRenderSky;
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void origins$updateChunksIfRenderChanged(CallbackInfo ci) {
        if (LevelRenderHelper.shouldReload(Minecraft.getInstance().player)) this.allChanged();
    }
}
