package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.builtin.regular.RecipePower;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.iafenvoy.origins.recipe.ModifiedCraftingRecipe;
import com.iafenvoy.origins.recipe.PowerCraftingRecipe;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    private RecipeMap recipes;
    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @ModifyReturnValue(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;", at = @At("RETURN"))
    private Optional<RecipeHolder<?>> modifyCraftingRecipe(Optional<RecipeHolder<?>> original, RecipeType<?> type, RecipeInput input, Level world) {
        return original.map(entry -> {
            // 26.1：RecipeHolder#id() 现在返回 ResourceKey<Recipe<?>>。
            Identifier id = entry.id().identifier();
            Recipe<?> recipe = entry.value();
            if (recipe instanceof CraftingRecipe craftingRecipe && ModifiedCraftingRecipe.canModify(id, craftingRecipe, input))
                return new RecipeHolder<>(entry.id(), new ModifiedCraftingRecipe(id, craftingRecipe));
            else return entry;
        });
    }

    // 26.1 移植：为 RecipePower 动态注入配方。旧的可变 byName 映射 /
    // RecipeManager#replaceRecipes 已移除（RecipeMap 不可变），因此我们不直接修改，/
    // 而是在原版应用后、finalizeRecipeLoading 之前在此处重建映射，/
    // 为每个已加载的 RecipePower 添加一个 PowerCraftingRecipe。PowerCraftingRecipe#matches /
    // 根据玩家是否实际激活该能力来限制合成。
    @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void origins$injectPowerRecipes(RecipeMap map, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci) {
        Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byId = new LinkedHashMap<>();
        this.recipes.values().forEach(holder -> byId.put(holder.id(), holder));
        Map<Identifier, Integer> priorities = new LinkedHashMap<>();

        PowerReference.listAllPowers(this.registries).forEach(holder -> {
            if (!(holder.power() instanceof RecipePower power)) return;
            int previousPriority = priorities.getOrDefault(holder.id(), Integer.MIN_VALUE);
            if (power.getPriority() < previousPriority) return;

            ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, holder.id());
            byId.put(key, new RecipeHolder<>(key, new PowerCraftingRecipe(holder.id(), power.getRecipe())));
            priorities.put(holder.id(), power.getPriority());
        });

        if (!priorities.isEmpty()) {
            this.recipes = RecipeMap.create(byId.values());
            Origins.LOGGER.info("Injected {} power recipes", priorities.size());
        }
    }
}
