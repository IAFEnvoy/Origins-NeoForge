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

    // 26.1：RecipeSerializer 现在是 record（MapCodec, StreamCodec），而非需要实现的接口。
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ModifiedCraftingRecipe>> MODIFIED =
            REGISTRY.register("modified", () -> new RecipeSerializer<>(ModifiedCraftingRecipe.CODEC, ModifiedCraftingRecipe.PACKET_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PowerCraftingRecipe>> POWER =
            REGISTRY.register("power", () -> new RecipeSerializer<>(PowerCraftingRecipe.CODEC, PowerCraftingRecipe.PACKET_CODEC));
}
