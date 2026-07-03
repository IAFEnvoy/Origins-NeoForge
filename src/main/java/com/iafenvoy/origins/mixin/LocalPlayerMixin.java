package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyAirSpeedPower;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventSprintingPower;
import com.iafenvoy.origins.data.power.builtin.regular.WaterVisionPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Unique
    private LocalPlayer origins$self() {
        return (LocalPlayer) (Object) this;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
    private float modifyFlySpeed(float original) {
        return PowerHelper.get(this.origins$self()).modify(ModifyAirSpeedPower.class, original);
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onGround()Z"), ordinal = 4)
    private boolean modifySprintAbility(boolean original) {
        return original && PowerHelper.get(this.origins$self()).noneActive(PreventSprintingPower.class);
    }

    @Inject(method = "getWaterVision", at = @At("HEAD"), cancellable = true)
    private void getUnderwaterVisibility(CallbackInfoReturnable<Float> info) {
        PowerHelper.get(this.origins$self()).streamActive(WaterVisionPower.class).map(WaterVisionPower::getStrength).max(Float::compareTo).ifPresent(info::setReturnValue);
    }
}
