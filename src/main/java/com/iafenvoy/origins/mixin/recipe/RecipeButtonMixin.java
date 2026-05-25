package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.recipe.ModifiedCraftingRecipe;
import com.iafenvoy.origins.recipe.PowerCraftingRecipe;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin {
    @Shadow
    private int currentIndex;
    @Shadow
    private RecipeCollection collection;
    @Shadow
    private RecipeBook book;

    @Shadow
    public abstract RecipeHolder<?> getRecipe();

    @Shadow
    protected abstract List<RecipeHolder<?>> getOrderedRecipes();

    @WrapOperation(method = {"renderWidget", "getTooltipText", "updateWidgetNarration"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeHolder;value()Lnet/minecraft/world/item/crafting/Recipe;"))
    private Recipe<?> modifyEntryQuery(RecipeHolder<?> entry, Operation<Recipe<?>> original, @Share("originalEntry") LocalRef<RecipeHolder<?>> sharedOriginalEntry) {
        sharedOriginalEntry.set(entry);
        ResourceLocation id = entry.id();
        Recipe<?> recipe = entry.value();
        if (recipe instanceof CraftingRecipe craftingRecipe && ModifiedCraftingRecipe.canModify(id, craftingRecipe, this.book))
            return new ModifiedCraftingRecipe(id, craftingRecipe);
        else return original.call(entry);
    }

    @ModifyExpressionValue(method = {"renderWidget", "getTooltipText", "updateWidgetNarration"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;getResultItem(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack modifyResultQuery(ItemStack original) {
        if (this.getOrderedRecipes().get(this.currentIndex).value() instanceof ModifiedCraftingRecipe r && this.book instanceof PowerCraftingObject pco)
            return pco.origins$getPlayer().map(x -> r.getModifiedResult(this.collection.registryAccess(), x)).map(Pair::getFirst).orElse(original);
        else return original;
    }

    @ModifyReturnValue(method = "getTooltipText", at = @At("RETURN"))
    private List<Component> appendRequiredRecipePowerTooltip(List<Component> original, @Share("originalEntry") LocalRef<RecipeHolder<?>> sharedOriginalEntry) {
        RecipeHolder<?> recipeEntry = sharedOriginalEntry.get() != null ? sharedOriginalEntry.get() : this.getRecipe();
        if (recipeEntry.value() instanceof PowerCraftingRecipe pcr && this.book instanceof PowerCraftingObject pco && pco.origins$getPlayer().isPresent()) {
            RegistryAccess access = pco.origins$getPlayer().get().registryAccess();
            access.registry(PowerRegistries.POWER_KEY).map(x -> x.get(pcr.powerId())).ifPresent(power -> {
                original.add(Component.empty());
                original.add(Component.translatable("tooltip.origins.power_recipe.required_power", power.getName(access)).withStyle(ChatFormatting.RED));
            });
        }
        return original;
    }
}
