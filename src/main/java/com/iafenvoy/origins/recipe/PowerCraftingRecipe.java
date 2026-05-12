package com.iafenvoy.origins.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.RecipePower;
import com.iafenvoy.origins.registry.OriginsRecipeSerializers;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record PowerCraftingRecipe(ResourceLocation powerId, CraftingRecipe delegate) implements CraftingRecipe {
    @Override
    public @NotNull CraftingBookCategory category() {
        return this.delegate().category();
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return input instanceof PowerCraftingObject pco && pco.origins$getPlayer() != null && OriginDataHolder.get(pco.origins$getPlayer()).hasActivePower(this.powerId(), RecipePower.class) && level.getRecipeManager().byKey(this.powerId())
                .filter(entry -> Objects.equals(this, entry.value()))
                .map(entry -> this.delegate().matches(input, level))
                .orElse(false);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider lookup) {
        return this.delegate().assemble(input, lookup);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return this.delegate().canCraftInDimensions(width, height);
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registriesLookup) {
        return this.delegate().getResultItem(registriesLookup);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return OriginsRecipeSerializers.POWER.get();
    }

    @Override
    public boolean isIncomplete() {
        return this.delegate().isIncomplete();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        return this.delegate().getRemainingItems(input);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.delegate().getIngredients();
    }

    @Override
    public boolean isSpecial() {
        return this.delegate().isSpecial();
    }

    @Override
    public boolean showNotification() {
        return this.delegate().showNotification();
    }

    @Override
    public @NotNull String getGroup() {
        return this.delegate().getGroup();
    }

    public static class Serializer implements RecipeSerializer<PowerCraftingRecipe> {
        public static final MapCodec<PowerCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("power").forGetter(PowerCraftingRecipe::powerId),
                MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(PowerCraftingRecipe::delegate)
        ).apply(instance, PowerCraftingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, PowerCraftingRecipe> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public @NotNull MapCodec<PowerCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, PowerCraftingRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}
