package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.iafenvoy.origins.render.LevelRenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    public abstract void allChanged();

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void skipSkyRenderingForPhasingBlindness(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if (camera.getEntity() instanceof LivingEntity living && PhasingPower.hasRenderMethod(living, PhasingPower.PhasingRenderType.BLINDNESS) && PhasingPower.getInWallBlockState(living) != null)
            ci.cancel();
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void updateChunksIfRenderChanged(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (LevelRenderHelper.shouldReload(Minecraft.getInstance().player)) this.allChanged();
    }
}
