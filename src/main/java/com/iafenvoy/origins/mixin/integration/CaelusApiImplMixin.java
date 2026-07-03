package com.iafenvoy.origins.mixin.integration;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventElytraFlightPower;
import com.illusivesoulworks.caelus.api.CaelusApi;
import com.illusivesoulworks.caelus.common.CaelusApiImpl;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CaelusApiImpl.class)
public class CaelusApiImplMixin {
    @Inject(method = "canFallFly(Lnet/minecraft/world/entity/LivingEntity;)Lcom/illusivesoulworks/caelus/api/CaelusApi$TriState;", at = @At("HEAD"), cancellable = true, remap = false)
    public void elytraFlightHook(LivingEntity livingEntity, CallbackInfoReturnable<CaelusApi.TriState> cir) {
        PowerHelper.get(livingEntity).execute(PreventElytraFlightPower.class, (h, p) -> {
            p.getEntityAction().execute(livingEntity);
            cir.setReturnValue(CaelusApi.TriState.DENY);
        });
    }
}
