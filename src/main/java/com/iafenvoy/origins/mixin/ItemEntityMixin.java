package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.prevent.PreventItemPickupPower;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Unique
    private ItemEntity origins$self() {
        return (ItemEntity) (Object) this;
    }

    @WrapOperation(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean apoli$onItemPickup(Inventory playerInventory, ItemStack stack, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
        if (PreventItemPickupPower.doesPrevent(this.origins$self(), player)) return false;
        else return original.call(playerInventory, stack);
    }
}
