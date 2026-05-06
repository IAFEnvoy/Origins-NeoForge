package com.iafenvoy.origins.util.codec;

import com.iafenvoy.origins.util.RecipeUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public final class MiscCodecs {
    public static final Codec<CraftingRecipe> DATAPACK_RECIPES_ONLY_CODEC = Recipe.CODEC.comapFlatMap(RecipeUtil::validateCraftingRecipe, Function.identity());

    public static MapCodec<OptionalInt> integer(String name) {
        return Codec.INT.optionalFieldOf(name).xmap(o -> o.map(OptionalInt::of).orElseGet(OptionalInt::empty), o -> o.isPresent() ? Optional.of(o.getAsInt()) : Optional.empty());
    }
}
