package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.render.OriginsRenderStateData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
public class ElytraLayerMixin {
    @Shadow @Final private ElytraModel elytraModel;
    @Shadow @Final private ElytraModel elytraBabyModel;
    @Shadow @Final private EquipmentLayerRenderer equipmentRenderer;

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void origins$renderPowerElytra(PoseStack poseStack, SubmitNodeCollector collector, int light, HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (!Boolean.TRUE.equals(state.getRenderData(OriginsRenderStateData.RENDER_ELYTRA))) return;

        ItemStack stack = state.chestEquipment;
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.assetId().isEmpty()) {
            stack = Items.ELYTRA.getDefaultInstance();
            equippable = stack.get(DataComponents.EQUIPPABLE);
        }
        if (equippable == null || equippable.assetId().isEmpty()) return;

        Identifier texture = state.getRenderData(OriginsRenderStateData.ELYTRA_TEXTURE);
        if (texture == null && state instanceof AvatarRenderState avatar) {
            if (avatar.skin.elytra() != null) texture = avatar.skin.elytra().texturePath();
            else if (avatar.skin.cape() != null && avatar.showCape) texture = avatar.skin.cape().texturePath();
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);
        this.equipmentRenderer.renderLayers(
                EquipmentClientInfo.LayerType.WINGS,
                equippable.assetId().orElseThrow(),
                state.isBaby ? this.elytraBabyModel : this.elytraModel,
                state,
                stack,
                poseStack,
                collector,
                light,
                texture,
                state.outlineColor,
                0
        );
        poseStack.popPose();
        ci.cancel();
    }
}
