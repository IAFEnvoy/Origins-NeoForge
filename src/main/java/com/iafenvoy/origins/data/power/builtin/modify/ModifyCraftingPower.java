package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.MiscUtil;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyCraftingPower extends Power {
    public static final MapCodec<ModifyCraftingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceLocation.CODEC.optionalFieldOf("recipe").forGetter(ModifyCraftingPower::getRecipe),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyCraftingPower::getItemCondition),
            ItemStack.CODEC.optionalFieldOf("result").forGetter(ModifyCraftingPower::getResult),
            ItemAction.optionalCodec("item_action").forGetter(ModifyCraftingPower::getItemAction),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyCraftingPower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ModifyCraftingPower::getBlockAction),
            ItemAction.optionalCodec("item_action_after_crafting").forGetter(ModifyCraftingPower::getItemActionAfterCrafting)
    ).apply(i, ModifyCraftingPower::new));

    private final Optional<ResourceLocation> recipe;
    private final ItemCondition itemCondition;
    private final Optional<ItemStack> result;
    private final ItemAction itemAction;
    private final EntityAction entityAction;
    private final BlockAction blockAction;
    private final ItemAction itemActionAfterCrafting;

    public ModifyCraftingPower(BaseSettings settings, Optional<ResourceLocation> recipe, ItemCondition itemCondition, Optional<ItemStack> result, ItemAction itemAction, EntityAction entityAction, BlockAction blockAction, ItemAction itemActionAfterCrafting) {
        super(settings);
        this.recipe = recipe;
        this.itemCondition = itemCondition;
        this.result = result;
        this.itemAction = itemAction;
        this.entityAction = entityAction;
        this.blockAction = blockAction;
        this.itemActionAfterCrafting = itemActionAfterCrafting;
    }

    public Optional<ResourceLocation> getRecipe() {
        return this.recipe;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public Optional<ItemStack> getResult() {
        return this.result;
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

    public ItemAction getItemActionAfterCrafting() {
        return this.itemActionAfterCrafting;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean doesApply(Entity entity, ResourceLocation targetRecipeId, ItemStack originalResultStack) {
        return this.recipe.map(targetRecipeId::equals).orElse(true)
                && this.itemCondition.test(entity.level(), originalResultStack);
    }

    public void applyAfterCraftingItemAction(Entity entity, SlotAccess outputStackReference) {
        this.itemActionAfterCrafting.execute(entity.level(), entity, outputStackReference);
    }

    public SlotAccess getNewResult(Entity entity, SlotAccess resultStackReference) {
        this.result.map(ItemStack::copy).ifPresent(resultStackReference::set);
        this.itemAction.execute(entity.level(), entity, resultStackReference);
        return resultStackReference;

    }

    public void executeActions(Entity entity, Optional<BlockPos> craftingBlockPos) {
        craftingBlockPos.ifPresent(pos -> this.blockAction.execute(entity.level(), pos, Direction.UP));
        this.entityAction.execute(entity);
    }

    public static ItemStack executeAfterCraftingAction(Player player, CraftingContainer recipeInput, Slot slot, ItemStack stack) {

        if (!(recipeInput instanceof PowerCraftingInventory pci)) {
            return stack;
        }

        SlotAccess stackReference = Mutable.stack(stack).toSlotAccess();
        List<ModifyCraftingPower> modifyCraftingPowers = pci.origins$getPowerTypes()
                .stream()
                .filter(ModifyCraftingPower.class::isInstance)
                .map(ModifyCraftingPower.class::cast)
                .toList();

        if (modifyCraftingPowers.isEmpty()) {
            return stack;
        }

        if (MiscUtil.hasSpaceInInventory(player, stack) && slot instanceof ResultSlot) {

            ItemStack copy = stack.copy();
            modifyCraftingPowers.forEach(mcpt -> mcpt.applyAfterCraftingItemAction(player, stackReference));

            if (stackReference.get().isEmpty()) {
                slot.onTake(player, copy);
            }

        }

        return stackReference.get();

    }
}
