package com.iafenvoy.origins.mixin.recipe;

import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Compatibility marker for the pre-26.1 recipe-button hooks. Recipe buttons
 * now render server-resolved displays identified only by RecipeDisplayId;
 * power-recipe visibility is therefore handled by the server recipe book and
 * modified crafting results are still calculated by ModifiedCraftingRecipe.
 */
@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin {
}
