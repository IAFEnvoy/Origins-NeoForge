package com.iafenvoy.origins.util;

import com.iafenvoy.origins.recipe.ModifiedCraftingRecipe;
import com.iafenvoy.origins.recipe.PowerCraftingRecipe;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class RecipeUtil {
    public static <R extends Recipe<?>> DataResult<R> validateRecipe(@NotNull R recipe) {
        return switch (recipe) {
            case ModifiedCraftingRecipe modifiedCraftingRecipe ->
                    createInternalOnlyError(modifiedCraftingRecipe.getSerializer());
            case PowerCraftingRecipe powerCraftingRecipe ->
                    createInternalOnlyError(powerCraftingRecipe.getSerializer());
            default -> DataResult.success(recipe);
        };
    }

    public static <R extends Recipe<?>> DataResult<CraftingRecipe> validateCraftingRecipe(@NotNull R recipe) {
        return validateRecipe(recipe).flatMap(r -> r instanceof CraftingRecipe craftingRecipe
                ? DataResult.success(craftingRecipe)
                : DataResult.error(() -> "Recipe is not a crafting recipe!"));
    }

    private static <R> DataResult<R> createInternalOnlyError(RecipeSerializer<?> serializer) {
        return DataResult.error(() -> "Recipe type \"" + BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer) + "\" is for internal use only!");
    }
}
