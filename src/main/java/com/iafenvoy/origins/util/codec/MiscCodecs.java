package com.iafenvoy.origins.util.codec;

import com.iafenvoy.origins.util.RecipeUtil;
import com.iafenvoy.origins.util.wrapper.OptionalBoolean;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class MiscCodecs {
    public static final Codec<CraftingRecipe> DATAPACK_RECIPES_ONLY_CODEC = Recipe.CODEC.comapFlatMap(RecipeUtil::validateCraftingRecipe, Function.identity());
    public static final Codec<Component> TRANSLATE_FIRST = Codec.either(Codec.STRING, ComponentSerialization.CODEC).xmap(x -> x.map(Component::translatable, Function.identity()), Either::right);
    public static final Codec<ParticleOptions> PARTICLE_OPTION_OR_SINGLE = Codec.either(BuiltInRegistries.PARTICLE_TYPE.byNameCodec(), ParticleTypes.CODEC).comapFlatMap(x -> x.map(p -> p instanceof ParticleOptions options ? DataResult.success(options) : DataResult.error(() -> "Only particles without fields can inline."), DataResult::success), Either::right);
    public static final Codec<Pattern> PATTERN = Codec.STRING.xmap(Pattern::compile, Pattern::pattern);

    public static MapCodec<OptionalBoolean> bool(String name) {
        return Codec.BOOL.optionalFieldOf(name).xmap(o -> o.map(OptionalBoolean::of).orElseGet(OptionalBoolean::empty), o -> o.isPresent() ? Optional.of(o.getAsBoolean()) : Optional.empty());
    }

    public static MapCodec<OptionalInt> integer(String name) {
        return Codec.INT.optionalFieldOf(name).xmap(o -> o.map(OptionalInt::of).orElseGet(OptionalInt::empty), o -> o.isPresent() ? Optional.of(o.getAsInt()) : Optional.empty());
    }
}
