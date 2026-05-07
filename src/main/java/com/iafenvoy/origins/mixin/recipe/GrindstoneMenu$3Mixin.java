package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.data.power.builtin.modify.ModifyGrindstonePower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/inventory/GrindstoneMenu$3")
public class GrindstoneMenu$3Mixin {
    @Unique
    private GrindstoneMenu origins$grindstoneHandler;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cacheGrindstone(GrindstoneMenu grindstoneScreenHandler, Container inventory, int i, int j, int k, CallbackInfo ci) {
        this.origins$grindstoneHandler = grindstoneScreenHandler;
    }

    @ModifyReturnValue(method = "mayPlace", at = @At("RETURN"))
    private boolean allowPowerStacks(boolean original, ItemStack stack) {
        return original || ModifyGrindstonePower.allowsInBottomSlot(this.origins$grindstoneHandler, stack);
    }
}
