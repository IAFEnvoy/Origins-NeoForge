package com.iafenvoy.origins.mixin.recipe;

import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Compatibility marker for the pre-26.1 client recipe filter.
 *
 * <p>Recipe collections now contain opaque {@code RecipeDisplayId} values, so
 * they no longer expose the parent recipe or power ID. Power recipes are
 * instead added to and removed from each player's server recipe book by
 * {@code RecipePower}, before display entries are synchronized.</p>
 */
@Mixin(RecipeCollection.class)
public abstract class RecipeCollectionMixin {
}
