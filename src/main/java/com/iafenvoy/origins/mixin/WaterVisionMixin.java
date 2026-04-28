package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.WaterVisionPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public class WaterVisionMixin {
    @Unique
    private LocalPlayer origins$self() {
        return (LocalPlayer) (Object) this;
    }

    @ModifyExpressionValue(method = "getWaterVision", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;waterVisionTime:I", ordinal = 0, opcode = Opcodes.GETFIELD))
    private int origins$ignoreVisibilityDelay(int original) {
        return !OriginDataHolder.get(this.origins$self()).hasPower(WaterVisionPower.class, true) ? 600 : original;
    }
}
