package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.InvisibilityPower;
import com.iafenvoy.origins.data.power.builtin.regular.ShakingPower;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"), ordinal = 2)
    private boolean preventOutlineRendering(boolean original, LivingEntity living) {
        if (OriginDataHolder.get(living).streamActivePowers(InvisibilityPower.class).noneMatch(InvisibilityPower::shouldRenderOutline))
            return false;
        return original;
    }

    @WrapWithCondition(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V"))
    private <T extends Entity> boolean preventFeatureRendering(RenderLayer<T, ?> instance, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        OriginDataHolder holder = OriginDataHolder.get(living);
        return holder.streamActivePowers(InvisibilityPower.class).anyMatch(InvisibilityPower::shouldRenderArmor);
    }

    @Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
    private void letPlayersShakeTheirBodies(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (OriginDataHolder.get(entity).hasPower(ShakingPower.class, true))
            cir.setReturnValue(true);
    }
}
