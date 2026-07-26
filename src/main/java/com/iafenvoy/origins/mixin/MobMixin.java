package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.prevent.PreventItemPickupPower;
import com.iafenvoy.origins.data.power.builtin.regular.PreventTargetingPower;
import com.iafenvoy.origins.data.power.builtin.regular.ModifyTargetRangePower;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {
    @Unique
    private Mob origins$self() {
        return (Mob) (Object) this;
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void origins$preventTargeting(LivingEntity target, CallbackInfo ci) {
        if (target != null && (PreventTargetingPower.prevents(target, this.origins$self())
                || !ModifyTargetRangePower.canTarget(this.origins$self(), target)))
            ci.cancel();
    }

    @WrapWithCondition(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
    private boolean origins$preventItemPickup(Mob instance, ItemEntity itemEntity) {
        return !PreventItemPickupPower.doesPrevent(itemEntity, instance);
    }
}
