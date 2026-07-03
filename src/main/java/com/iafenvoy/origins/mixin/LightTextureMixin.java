package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyVariable(method = "updateLightTexture", at = @At(value = "STORE"), ordinal = 7)
    private float nightVisionPowerEffect(float value) {
        if (this.minecraft.player == null) return value;
        return PowerHelper.get(this.minecraft.player).streamActive(NightVisionPower.class).map(NightVisionPower::getStrength).max(Float::compareTo).map(x -> Math.max(x, value)).orElse(value);
    }
}
