package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.data._common.helper.RecipeHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.mixin.recipe.RecipeManagerAccessor;
import com.iafenvoy.origins.recipe.PowerCraftingRecipe;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class RecipePower extends Power implements Prioritized, RecipeHelper {
    public static final MapCodec<RecipePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(RecipePower::getRecipe),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(RecipePower::getPriority)
    ).apply(i, RecipePower::new));
    private final CraftingRecipe recipe;
    private final int priority;

    public RecipePower(BaseSettings settings, CraftingRecipe recipe, int priority) {
        super(settings);
        this.recipe = recipe;
        this.priority = priority;
    }

    @Override
    public CraftingRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static void registerPowerRecipes(ReloadableServerResources resources) {
        RecipeManager recipeManager = resources.getRecipeManager();

        Map<ResourceLocation, RecipeHolder<?>> recipeEntriesById = new Object2ObjectOpenHashMap<>(((RecipeManagerAccessor) recipeManager).getByName());
        Object2IntMap<ResourceLocation> priorityEntries = new Object2IntOpenHashMap<>();

        List<PowerHolder> powers = PowerReference.listAllPowers(resources.getRegistryLookup()).filter(x -> x.power() instanceof RecipePower).toList();

        for (PowerHolder power : powers) {
            if (!(power.power() instanceof RecipePower recipePower)) continue;
            ResourceLocation powerId = power.id();

            //  Only register the power recipe if no other recipes have the same ID
            if (!priorityEntries.containsKey(powerId) || priorityEntries.getInt(powerId) < recipePower.priority)
                recipeEntriesById.put(powerId, new RecipeHolder<>(powerId, new PowerCraftingRecipe(powerId, recipePower.recipe)));

            priorityEntries.put(powerId, recipePower.priority);
        }
        recipeManager.replaceRecipes(recipeEntriesById.values());
    }
}
