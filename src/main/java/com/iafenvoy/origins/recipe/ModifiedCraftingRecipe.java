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
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record ModifiedCraftingRecipe(Identifier id, CraftingRecipe delegate) implements CraftingRecipe {
    public static final MapCodec<ModifiedCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ModifiedCraftingRecipe::id),
            MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(ModifiedCraftingRecipe::delegate))
            .apply(instance, ModifiedCraftingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ModifiedCraftingRecipe> PACKET_CODEC = ByteBufCodecs
            .fromCodecWithRegistries(CODEC.codec());

    @Override
    public @NotNull CraftingBookCategory category() {
        return this.delegate().category();
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level world) {
        return this.delegate().matches(input, world);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
        if (input instanceof PowerCraftingInventory pci && pci.origins$getPlayer().isPresent()) {
            Pair<ItemStack, Collection<ModifyCraftingPower>> result = getModifiedResult(this.id(), this.delegate(),
                    input, pci.origins$getPlayer().get());
            pci.origins$setPowerTypes(result.getSecond());
            return result.getFirst().copy();
        } else
            return this.delegate().assemble(input).copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return OriginsRecipeSerializers.MODIFIED.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return this.delegate().placementInfo();
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        return this.delegate().display();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        return this.delegate().getRemainingItems(input);
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
    public @NotNull String group() {
        return this.delegate().group();
    }

    public static boolean canModify(Identifier id, CraftingRecipe craftingRecipe, RecipeInput recipeInput) {
        if (!(recipeInput instanceof PowerCraftingObject pco))
            return false;
        // 26.1：没有静态结果访问器；从（已匹配的）输入中合成。
        ItemStack result = recipeInput instanceof CraftingInput ci ? craftingRecipe.assemble(ci) : ItemStack.EMPTY;
        return pco
                .origins$getPlayer().map(p -> p != null && OriginDataHolder.get(p)
                        .streamActivePowers(ModifyCraftingPower.class).anyMatch(mcpt -> mcpt.doesApply(p, id, result)))
                .orElse(false);
    }

    public static Pair<ItemStack, Collection<ModifyCraftingPower>> getModifiedResult(Identifier id,
            CraftingRecipe craftingRecipe, CraftingInput input, @NotNull Player player) {
        ItemStack resultStack = craftingRecipe.assemble(input).copy();
        SlotAccess newStackRef = Mutable.stack(resultStack).toSlotAccess();

        List<ModifyCraftingPower> powers = OriginDataHolder.get(player).streamActivePowers(ModifyCraftingPower.class)
                .filter(mcpt -> mcpt.doesApply(player, id, resultStack)).toList();
        powers.forEach(mcpt -> mcpt.getNewResult(player, newStackRef));
        return Pair.of(newStackRef.get(), powers);
    }

    public static Optional<BlockPos> getBlockFromInventory(TransientCraftingContainer craftingInventory) {
        if (((TransientCraftingContainerAccessor) craftingInventory)
                .getMenu() instanceof CraftingMenu craftingScreenHandler)
            return ((CraftingMenuAccessor) craftingScreenHandler).getAccess().evaluate((world, pos) -> pos);
        else
            return Optional.empty();
    }
}
