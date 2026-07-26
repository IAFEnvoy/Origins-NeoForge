package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.RiptidePower;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityRiptideMixin {
    @Inject(method = "isAutoSpinAttack", at = @At("RETURN"), cancellable = true)
    private void origins$allowRiptideFromPower(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue())
            cir.setReturnValue(PowerHelper.get((LivingEntity) (Object) this).anyActive(RiptidePower.class));
    }
}
