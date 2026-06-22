package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Ports NightVisionPower to Minecraft 26.1's extracted lightmap state. */
@Mixin(LightmapRenderStateExtractor.class)
public class LightTextureMixin {
    @Inject(method = "extract", at = @At("TAIL"))
    private void origins$applyNightVisionPower(LightmapRenderState state, float partialTicks, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) return;
        OriginDataHolder.get(Minecraft.getInstance().player).streamActivePowers(NightVisionPower.class)
                .map(NightVisionPower::getStrength)
                .max(Float::compareTo)
                .ifPresent(strength -> state.nightVisionEffectIntensity = Math.max(state.nightVisionEffectIntensity, strength));
    }
}
