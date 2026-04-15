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

public class ModifyCraftingPower extends Power {
    public static final MapCodec<ModifyCraftingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceLocation.CODEC.optionalFieldOf("recipe").forGetter(ModifyCraftingPower::getRecipeLocation),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyCraftingPower::getItemCondition),
            ItemStack.CODEC.optionalFieldOf("result").forGetter(ModifyCraftingPower::getNewStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyCraftingPower::getItemAction),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyCraftingPower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ModifyCraftingPower::getBlockAction),
            ItemAction.optionalCodec("item_action_after_crafting").forGetter(ModifyCraftingPower::getAfterCraftingItemAction)
    ).apply(i, ModifyCraftingPower::new));

    private final Optional<ResourceLocation> recipeLocation;
    private final ItemCondition itemCondition;
    private final Optional<ItemStack> newStack;
    private final ItemAction itemAction;
    private final EntityAction entityAction;
    private final BlockAction blockAction;
    private final ItemAction afterCraftingItemAction;

    public ModifyCraftingPower(BaseSettings settings, Optional<ResourceLocation> recipeLocation, ItemCondition itemCondition, Optional<ItemStack> newStack, ItemAction itemAction, EntityAction entityAction, BlockAction blockAction, ItemAction afterCraftingItemAction) {
        super(settings);
        this.recipeLocation = recipeLocation;
        this.itemCondition = itemCondition;
        this.newStack = newStack;
        this.itemAction = itemAction;
        this.entityAction = entityAction;
        this.blockAction = blockAction;
        this.afterCraftingItemAction = afterCraftingItemAction;
    }

    public Optional<ResourceLocation> getRecipeLocation() {
        return this.recipeLocation;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public Optional<ItemStack> getNewStack() {
        return this.newStack;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public ItemAction getAfterCraftingItemAction() {
        return this.afterCraftingItemAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    // Recipe identification in 1.21.1 uses RecipeInput instead of CraftingContainer

    public boolean doesApply(RecipeInput container, Recipe<? super RecipeInput> recipe, Level level) {
        return (this.getRecipeLocation().isEmpty() || this.getRecipeLocation().get().equals(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()))) &&
                (this.getItemCondition().test(level, recipe.assemble(container, level.registryAccess())));
    }

    public ItemStack createResult(RecipeInput container, Recipe<? super RecipeInput> recipe, Entity entity, Level level) {
        ItemStack stack;
        if (this.getNewStack().isPresent())
            stack = this.getNewStack().get().copy();
        else
            stack = recipe.assemble(container, level.registryAccess());
        this.getItemAction().execute(level, entity, stack);
        return stack;
    }

    public void execute(Entity entity, @Nullable BlockPos pos) {
        if (pos != null)
            this.getBlockAction().execute(entity.level(), pos, Direction.UP);
        this.getEntityAction().execute(entity);
    }

    public void executeAfterCraftingAction(Level level, Entity entity, ItemStack stack) {
        this.getAfterCraftingItemAction().execute(level, entity, stack);
    }
}
