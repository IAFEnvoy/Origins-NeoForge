package com.iafenvoy.origins.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.iafenvoy.origins.mixin.recipe.CraftingMenuAccessor;
import com.iafenvoy.origins.mixin.recipe.TransientCraftingContainerAccessor;
import com.iafenvoy.origins.registry.OriginsRecipeSerializers;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record ModifiedCraftingRecipe(ResourceLocation id, CraftingRecipe delegate) implements CraftingRecipe {

    @Override
    public @NotNull CraftingBookCategory category() {
        return this.delegate().category();
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level world) {
        return this.delegate().matches(input, world);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider lookup) {
        if (input instanceof PowerCraftingInventory pci) {
            Pair<ItemStack, Collection<ModifyCraftingPower>> result = this.getModifiedResult(lookup, pci.origins$getPlayer());
            pci.origins$setPowerTypes(result.getSecond());
            return result.getFirst().copy();
        } else return this.getResultItem(lookup).copy();
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
        return OriginsRecipeSerializers.MODIFIED.get();
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

    public Pair<ItemStack, Collection<ModifyCraftingPower>> getModifiedResult(HolderLookup.Provider registriesLookup, @Nullable Player player) {
        return getModifiedResult(this.id(), this.delegate(), registriesLookup, player);
    }

    public static boolean canModify(ResourceLocation id, CraftingRecipe craftingRecipe, RecipeBook recipeBook) {
        return recipeBook instanceof PowerCraftingObject pco && canModify(id, craftingRecipe, pco.origins$getPlayer());
    }

    public static boolean canModify(ResourceLocation id, CraftingRecipe craftingRecipe, RecipeInput recipeInput) {
        return recipeInput instanceof PowerCraftingObject pco && canModify(id, craftingRecipe, pco.origins$getPlayer());
    }

    public static boolean canModify(ResourceLocation id, CraftingRecipe craftingRecipe, @Nullable Player player) {
        return player != null && OriginDataHolder.get(player).streamActivePowers(ModifyCraftingPower.class).anyMatch(mcpt -> mcpt.doesApply(player, id, craftingRecipe.getResultItem(player.registryAccess())));
    }

    public static Pair<ItemStack, Collection<ModifyCraftingPower>> getModifiedResult(ResourceLocation id, CraftingRecipe craftingRecipe, HolderLookup.Provider registriesLookup, @Nullable Player player) {
        ItemStack resultStack = craftingRecipe.getResultItem(registriesLookup).copy();
        SlotAccess newStackRef = Mutable.stack(resultStack).toSlotAccess();

        List<ModifyCraftingPower> powers = OriginDataHolder.get(player).streamActivePowers(ModifyCraftingPower.class).filter(mcpt -> mcpt.doesApply(player, id, resultStack)).toList();
        powers.forEach(mcpt -> mcpt.getNewResult(player, newStackRef));
        return Pair.of(newStackRef.get(), powers);
    }

    public static Optional<BlockPos> getBlockFromInventory(TransientCraftingContainer craftingInventory) {
        if (((TransientCraftingContainerAccessor) craftingInventory).getMenu() instanceof CraftingMenu craftingScreenHandler)
            return ((CraftingMenuAccessor) craftingScreenHandler).getAccess().evaluate((world, pos) -> pos);
        else return Optional.empty();
    }

    public static class Serializer implements RecipeSerializer<ModifiedCraftingRecipe> {
        public static final MapCodec<ModifiedCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ModifiedCraftingRecipe::id),
                MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(ModifiedCraftingRecipe::delegate)
        ).apply(instance, ModifiedCraftingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ModifiedCraftingRecipe> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public @NotNull MapCodec<ModifiedCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ModifiedCraftingRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}
