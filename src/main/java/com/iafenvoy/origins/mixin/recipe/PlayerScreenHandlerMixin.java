package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.iafenvoy.origins.data.power.builtin.regular.RestrictArmorPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Optional;

@Mixin(InventoryMenu.class)
public abstract class PlayerScreenHandlerMixin {
    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/world/inventory/AbstractContainerMenu;II)Lnet/minecraft/world/inventory/TransientCraftingContainer;"))
    private TransientCraftingContainer origins$cachePlayerToCraftingInventory(TransientCraftingContainer original, Inventory playerInventory) {

        if (original instanceof PowerCraftingInventory pci) {
            pci.origins$setPlayer(playerInventory.player);
        }

        return original;

    }

    @ModifyExpressionValue(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;hasItem()Z", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EquipmentSlot$Type;HUMANOID_ARMOR:Lnet/minecraft/world/entity/EquipmentSlot$Type;")))
    private boolean origins$disallowQuickMovingRestrictedWearables(boolean original, Player player, @Local(ordinal = 1) ItemStack stackToInsert, @Local EquipmentSlot slot) {
        return original || OriginDataHolder.get(player).streamActivePowers(RestrictArmorPower.class).anyMatch(x -> Optional.ofNullable(x.getConditions().get(slot)).map(c -> c.test(player.level(), stackToInsert)).orElse(false));
    }

    @ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0), ordinal = 1)
    private ItemStack origins$modifyResultStackOnQuickMove(ItemStack original, Player player, int slotId, @Local Slot slot) {
        return ModifyCraftingPower.executeAfterCraftingAction(player, this.craftSlots, slot, original);
    }

}
