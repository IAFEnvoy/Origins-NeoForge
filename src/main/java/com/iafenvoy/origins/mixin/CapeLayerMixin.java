package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.render.OriginsRenderStateData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void origins$hideCapeForPowerElytra(PoseStack poseStack, SubmitNodeCollector collector, int light, AvatarRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (Boolean.TRUE.equals(state.getRenderData(OriginsRenderStateData.RENDER_ELYTRA))) ci.cancel();
    }
}
