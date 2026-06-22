package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.builtin.regular.ElytraFlightPower;
import com.iafenvoy.origins.data.power.builtin.regular.InvisibilityPower;
import com.iafenvoy.origins.data.power.builtin.regular.ModelColorPower;
import com.iafenvoy.origins.data.power.builtin.regular.ShakingPower;
import com.iafenvoy.origins.render.OriginsRenderStateData;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Shadow
    public abstract Identifier getTextureLocation(LivingEntityRenderState state);

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void origins$extractPowerRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        ModelColorPower.getColor(entity).ifPresent(color -> state.setRenderData(OriginsRenderStateData.MODEL_COLOR, color));

        var invisibility = OriginDataHolder.get(entity).streamActivePowers(InvisibilityPower.class).toList();
        if (!invisibility.isEmpty()) {
            state.setRenderData(OriginsRenderStateData.HIDE_LAYERS, invisibility.stream().noneMatch(InvisibilityPower::shouldRenderArmor));
            state.setRenderData(OriginsRenderStateData.HIDE_OUTLINE, invisibility.stream().noneMatch(InvisibilityPower::shouldRenderOutline));
        }

        if (OriginDataHolder.get(entity).hasActivePower(ShakingPower.class)) {
            state.setRenderData(OriginsRenderStateData.SHAKING, true);
        }

        OriginDataHolder.get(entity).streamActivePowers(ElytraFlightPower.class)
                .filter(ElytraFlightPower::shouldRenderElytra)
                .findFirst()
                .ifPresent(power -> {
                    state.setRenderData(OriginsRenderStateData.RENDER_ELYTRA, true);
                    power.getTextureLocation().ifPresent(texture -> state.setRenderData(OriginsRenderStateData.ELYTRA_TEXTURE, texture));
                });

        if (Boolean.TRUE.equals(state.getRenderData(OriginsRenderStateData.HIDE_OUTLINE))) state.outlineColor = 0;
    }

    @ModifyReturnValue(method = "isShaking", at = @At("RETURN"))
    private boolean origins$applyShakingPower(boolean original, LivingEntityRenderState state) {
        return original || Boolean.TRUE.equals(state.getRenderData(OriginsRenderStateData.SHAKING));
    }

    @ModifyReturnValue(method = "shouldRenderLayers", at = @At("RETURN"))
    private boolean origins$applyInvisibleArmor(boolean original, LivingEntityRenderState state) {
        return original && !Boolean.TRUE.equals(state.getRenderData(OriginsRenderStateData.HIDE_LAYERS));
    }

    @ModifyReturnValue(method = "getModelTint", at = @At("RETURN"))
    private int origins$applyModelColor(int original, LivingEntityRenderState state) {
        ColorSettings color = state.getRenderData(OriginsRenderStateData.MODEL_COLOR);
        return color == null ? original : color.merge(original).getIntValue();
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void origins$useTranslucentLayer(LivingEntityRenderState state, boolean visible, boolean transparent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        ColorSettings color = state.getRenderData(OriginsRenderStateData.MODEL_COLOR);
        if (visible && color != null && color.a().map(alpha -> alpha < 1.0F).orElse(false)) {
            cir.setReturnValue(RenderTypes.entityTranslucent(this.getTextureLocation(state)));
        }
    }
}
