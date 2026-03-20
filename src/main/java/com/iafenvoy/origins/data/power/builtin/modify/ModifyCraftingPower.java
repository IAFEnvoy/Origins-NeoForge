package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyCraftingPower(Optional<ResourceLocation> recipeLocation, ItemCondition itemCondition, Optional<ItemStack> newStack,
                                  ItemAction itemAction, EntityAction entityAction, BlockAction blockAction,ItemAction afterCraftingItemAction) implements Power {

    public static final MapCodec<ModifyCraftingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.optionalFieldOf("recipe").forGetter(ModifyCraftingPower::recipeLocation),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyCraftingPower::itemCondition),
            ItemStack.CODEC.optionalFieldOf("result").forGetter(ModifyCraftingPower::newStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyCraftingPower::itemAction),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyCraftingPower::entityAction),
            BlockAction.optionalCodec("block_action").forGetter(ModifyCraftingPower::blockAction),
            ItemAction.optionalCodec("item_action_after_crafting").forGetter(ModifyCraftingPower::afterCraftingItemAction)
    ).apply(i, ModifyCraftingPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    // Recipe identification in 1.21.1 uses RecipeInput instead of CraftingContainer

    public boolean doesApply(RecipeInput container, Recipe<? super RecipeInput> recipe, Level level) {
        return (this.recipeLocation().isEmpty() || this.recipeLocation().get().equals(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()))) &&
                (this.itemCondition().test(level, recipe.assemble(container, level.registryAccess())));
    }

    public ItemStack createResult(RecipeInput container, Recipe<? super RecipeInput> recipe, Entity entity, Level level) {
        ItemStack stack;
        if (this.newStack().isPresent())
            stack = this.newStack().get().copy();
        else
            stack = recipe.assemble(container, level.registryAccess());
        this.itemAction().execute(level,entity, stack);
        return stack;
    }

    public void execute(Entity entity, @Nullable BlockPos pos) {
        if (pos != null)
            this.blockAction().execute(entity.level(), pos, Direction.UP);
        this.entityAction().execute(entity);
    }

    public void executeAfterCraftingAction(Level level,Entity entity, ItemStack stack) {
        this.afterCraftingItemAction().execute(level,entity, stack);
    }
}
