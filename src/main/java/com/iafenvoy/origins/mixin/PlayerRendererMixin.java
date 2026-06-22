package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.builtin.regular.ModelColorPower;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin {
    @Inject(
            method = "renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;ZLnet/minecraft/client/player/AbstractClientPlayer;)V",
            at = @At("HEAD"), cancellable = true
    )
    private void origins$colorRightHand(PoseStack poseStack, SubmitNodeCollector collector, int light, Identifier texture, boolean sleeve, AbstractClientPlayer player, CallbackInfo ci) {
        if (this.origins$submitColoredHand(poseStack, collector, light, texture, sleeve, player, true)) ci.cancel();
    }

    @Inject(
            method = "renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;ZLnet/minecraft/client/player/AbstractClientPlayer;)V",
            at = @At("HEAD"), cancellable = true
    )
    private void origins$colorLeftHand(PoseStack poseStack, SubmitNodeCollector collector, int light, Identifier texture, boolean sleeve, AbstractClientPlayer player, CallbackInfo ci) {
        if (this.origins$submitColoredHand(poseStack, collector, light, texture, sleeve, player, false)) ci.cancel();
    }

    private boolean origins$submitColoredHand(PoseStack poseStack, SubmitNodeCollector collector, int light, Identifier texture, boolean sleeve, AbstractClientPlayer player, boolean right) {
        Optional<ColorSettings> color = ModelColorPower.getColor(player);
        if (color.isEmpty()) return false;

        PlayerModel model = (PlayerModel) ((AvatarRenderer<?>) (Object) this).getModel();
        ModelPart arm = right ? model.rightArm : model.leftArm;
        arm.resetPose();
        arm.visible = true;
        model.leftSleeve.visible = sleeve;
        model.rightSleeve.visible = sleeve;
        model.leftArm.zRot = -0.1F;
        model.rightArm.zRot = 0.1F;
        collector.submitModelPart(arm, poseStack, RenderTypes.entityTranslucent(texture), light, OverlayTexture.NO_OVERLAY, null, color.get().getIntValue(), null);
        return true;
    }
}
