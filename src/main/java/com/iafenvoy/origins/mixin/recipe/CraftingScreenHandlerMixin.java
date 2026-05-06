package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.accessor.ScreenHandlerUsabilityOverride;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

@Mixin(CraftingMenu.class)
public abstract class CraftingScreenHandlerMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> implements ScreenHandlerUsabilityOverride {
    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @Shadow
    @Final
    private Player player;
    @Unique
    private boolean origins$canUse = false;

    @Override
    public boolean origins$canUse() {
        return this.origins$canUse;
    }

    @Override
    public void origins$canUse(boolean canUse) {
        this.origins$canUse = canUse;
    }

    private CraftingScreenHandlerMixin(MenuType screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @ModifyExpressionValue(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "NEW", target = "(Lnet/minecraft/world/inventory/AbstractContainerMenu;II)Lnet/minecraft/world/inventory/TransientCraftingContainer;"))
    private TransientCraftingContainer origins$cachePlayerToCraftingInventory(TransientCraftingContainer original, int syncId, Inventory playerInventory) {

        if (original instanceof PowerCraftingInventory pci) {
            pci.origins$setPlayer(playerInventory.player);
        }

        return original;

    }

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;"))
    private static void origins$clearPowerCraftingInventory(AbstractContainerMenu handler, Level world, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory, @Nullable RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {

        if (craftingInventory instanceof PowerCraftingInventory pci) {
            pci.origins$setPowerTypes(new LinkedList<>());
        }

    }

    @ModifyReturnValue(method = "stillValid", at = @At("RETURN"))
    private boolean origins$allowUsingViaPower(boolean original, Player playerEntity) {
        return original || this.origins$canUse();
    }

    @ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/CraftingMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0), ordinal = 1)
    private ItemStack origins$modifyResultStackOnQuickMove(ItemStack original, Player player, int slotId, @Local Slot slot) {
        return ModifyCraftingPower.executeAfterCraftingAction(player, this.craftSlots, slot, original);
    }

}
