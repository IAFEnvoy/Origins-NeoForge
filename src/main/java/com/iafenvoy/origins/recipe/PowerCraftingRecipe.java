package com.iafenvoy.origins.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.RecipePower;
import com.iafenvoy.origins.registry.OriginsRecipeSerializers;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// 26.1：已移植到重写的 Recipe 接口（assemble 丢失了 lookup 参数；
// getResultItem/getIngredients/canCraftInDimensions/isIncomplete 已移除；
// getGroup -> group；新增 placementInfo/display；RecipeSerializer 现在是 record）。
public record PowerCraftingRecipe(Identifier powerId, CraftingRecipe delegate) implements CraftingRecipe {
    public static final MapCodec<PowerCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("power").forGetter(PowerCraftingRecipe::powerId),
            MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(PowerCraftingRecipe::delegate)
    ).apply(instance, PowerCraftingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PowerCraftingRecipe> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    @Override
    public @NotNull CraftingBookCategory category() {
        return this.delegate().category();
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return input instanceof PowerCraftingObject pco
                && pco.origins$getPlayer().map(OriginDataHolder::get).map(h -> h.hasActivePower(this.powerId(), RecipePower.class)).orElse(false)
                && this.delegate().matches(input, level);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
        return this.delegate().assemble(input);
    }

    @Override
    public @NotNull RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return OriginsRecipeSerializers.POWER.get();
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
}
