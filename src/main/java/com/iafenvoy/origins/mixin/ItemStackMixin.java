package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.regular.ItemOnItemPower;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "overrideOtherStackedOnMe", at = @At("RETURN"), cancellable = true)
    public void onItemOnItem(ItemStack other, Slot slot, ClickAction action, Player pPlayer, SlotAccess otherAccess, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (ItemOnItemPower.execute(pPlayer, slot, otherAccess, action))
            cir.setReturnValue(true);
    }
}
