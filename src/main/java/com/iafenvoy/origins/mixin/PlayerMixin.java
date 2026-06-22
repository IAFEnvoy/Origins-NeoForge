package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyAirSpeedPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyExhaustionPower;
import com.iafenvoy.origins.data.power.builtin.regular.WaterBreathingPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class PlayerMixin {
    @Unique
    private Player origins$self() {
        return (Player) (Object) this;
    }

    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyExhaustion(float exhaustion) {
        return OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyExhaustionPower.class, exhaustion);
    }

    @ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
    private float modifyFlySpeed(float original) {
        return OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyAirSpeedPower.class, original);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean origins$submergedProxy(boolean original) {
        return original ^ OriginDataHolder.get(this.origins$self()).hasActivePower(WaterBreathingPower.class);
    }
}
