package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.prevent.PreventItemPickupPower;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {
    @WrapWithCondition(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
    private boolean apoli$preventItemPickup(Mob instance, ItemEntity itemEntity) {
        return !PreventItemPickupPower.doesPrevent(itemEntity, instance);
    }
}
