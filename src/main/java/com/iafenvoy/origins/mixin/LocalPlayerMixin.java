package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyAirSpeedPower;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventSprintingPower;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Unique
    private LocalPlayer origins$self() {
        return (LocalPlayer) (Object) this;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
    private float modifyFlySpeed(Abilities abilities) {
        return OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyAirSpeedPower.class, abilities.getFlyingSpeed());
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onGround()Z"), ordinal = 4)
    private boolean modifySprintAbility(boolean original) {
        return original && !OriginDataHolder.get(this.origins$self()).hasPower(PreventSprintingPower.class, true);
    }
}
