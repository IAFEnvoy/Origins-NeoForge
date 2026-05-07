package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
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
        return OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyAirSpeedPower.class, original);
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onGround()Z"), ordinal = 4)
    private boolean modifySprintAbility(boolean original) {
        return original && !OriginDataHolder.get(this.origins$self()).hasActivePower(PreventSprintingPower.class);
    }

    @Inject(method = "getWaterVision", at = @At("HEAD"), cancellable = true)
    private void getUnderwaterVisibility(CallbackInfoReturnable<Float> info) {
        OriginDataHolder.get(this.origins$self()).streamActivePowers(WaterVisionPower.class).map(WaterVisionPower::getStrength).max(Float::compareTo).ifPresent(info::setReturnValue);
    }
}
