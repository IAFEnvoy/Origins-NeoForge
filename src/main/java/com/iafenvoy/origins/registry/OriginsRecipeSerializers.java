package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.recipe.ModifiedCraftingRecipe;
import com.iafenvoy.origins.recipe.PowerCraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Origins.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, ModifiedCraftingRecipe.Serializer> MODIFIED = REGISTRY.register("modified", ModifiedCraftingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, PowerCraftingRecipe.Serializer> POWER = REGISTRY.register("power", PowerCraftingRecipe.Serializer::new);
}
