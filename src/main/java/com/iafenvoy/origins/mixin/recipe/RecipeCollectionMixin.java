package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.RecipePower;
import com.iafenvoy.origins.recipe.PowerCraftingRecipe;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeCollection.class)
public abstract class RecipeCollectionMixin {
    @ModifyExpressionValue(method = "canCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/StackedContents;canCraft(Lnet/minecraft/world/item/crafting/Recipe;Lit/unimi/dsi/fastutil/ints/IntList;)Z"))
    private boolean accountForPowerRecipes(boolean original, StackedContents recipeFinder, int gridWidth, int gridHeight, RecipeBook recipeBook, @Local RecipeHolder<?> recipeEntry) {
        return original && recipeEntry.value() instanceof PowerCraftingRecipe pcr && recipeBook instanceof PowerCraftingObject pco && pco.origins$getPlayer() != null ? OriginDataHolder.get(pco.origins$getPlayer()).hasActivePower(pcr.powerId(), RecipePower.class) : original;
    }
}
