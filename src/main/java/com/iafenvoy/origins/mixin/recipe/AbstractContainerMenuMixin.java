package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.accessor.PowerModifiedGrindstone;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyGrindstonePower;
import com.iafenvoy.origins.recipe.ModifiedCraftingRecipe;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @ModifyExpressionValue(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;tryRemove(IILnet/minecraft/world/entity/player/Player;)Ljava/util/Optional;"))
    private Optional<ItemStack> performAfterCraftingActions(Optional<ItemStack> original, int slotIndex, int button, ClickType actionType, Player player, @Local Slot slot) {
        if ((AbstractContainerMenu) (Object) this instanceof PowerModifiedGrindstone pmg && original.isPresent() && slotIndex == 2) {
            List<ModifyGrindstonePower> applyingPowers = pmg.origins$getAppliedPowers();
            if (applyingPowers == null || applyingPowers.isEmpty()) return original;

            SlotAccess stackReference = Mutable.stack(original.get()).toSlotAccess();
            applyingPowers.forEach(mgpt -> mgpt.executeActions(player, pmg.origins$getPos(), stackReference));

            return Optional.of(stackReference.get());
        } else if (original.isPresent() && slot instanceof ResultSlot resultSlot) {
            if (!(((ResultSlotAccessor) resultSlot).getCraftSlots() instanceof TransientCraftingContainer craftingInventory) || !(craftingInventory instanceof PowerCraftingInventory pci))
                return original;

            List<ModifyCraftingPower> modifyCraftingPowers = pci.origins$getPowerTypes()
                    .stream()
                    .filter(ModifyCraftingPower.class::isInstance)
                    .map(ModifyCraftingPower.class::cast)
                    .toList();
            if (modifyCraftingPowers.isEmpty()) return original;

            modifyCraftingPowers.forEach(mcpt -> mcpt.executeActions(player, ModifiedCraftingRecipe.getBlockFromInventory(craftingInventory)));
            SlotAccess stackReference = Mutable.stack(original.get()).toSlotAccess();

            modifyCraftingPowers.forEach(mcpt -> mcpt.applyAfterCraftingItemAction(player, stackReference));
            return Optional.of(stackReference.get());
        }
        return original;
    }
}
