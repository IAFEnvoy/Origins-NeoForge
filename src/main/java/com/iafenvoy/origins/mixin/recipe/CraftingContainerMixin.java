package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingContainer.class)
public interface CraftingContainerMixin {
    @ModifyReturnValue(method = "asPositionedCraftInput", at = @At("RETURN"))
    private CraftingInput.Positioned passCacheToPositionedInput(CraftingInput.Positioned original) {
        if ((CraftingContainer) this instanceof PowerCraftingInventory sourcePci && original.input() instanceof PowerCraftingInventory targetPci) {
            targetPci.origins$setPowerTypes(sourcePci.origins$getPowerTypes());
            targetPci.origins$setPlayer(sourcePci.origins$getPlayer());
            targetPci.origins$setInventory(sourcePci.origins$getInventory());
        }
        return original;
    }
}
