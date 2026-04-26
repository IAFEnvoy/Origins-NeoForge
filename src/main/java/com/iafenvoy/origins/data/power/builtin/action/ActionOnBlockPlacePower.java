package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@NotImplementedYet
public class ActionOnBlockPlacePower extends Power {
    public static final MapCodec<ActionOnBlockPlacePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(ActionOnBlockPlacePower::getSettings),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnBlockPlacePower::getEntityAction),
            ItemAction.optionalCodec("held_item_action").forGetter(ActionOnBlockPlacePower::getHeldItemAction),
            BlockAction.optionalCodec("place_to_action").forGetter(ActionOnBlockPlacePower::getPlaceToAction),
            BlockAction.optionalCodec("place_on_action").forGetter(ActionOnBlockPlacePower::getPlaceOnAction),
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnBlockPlacePower::getItemCondition),
            BlockCondition.optionalCodec("place_to_condition").forGetter(ActionOnBlockPlacePower::getPlaceToCondition),
            BlockCondition.optionalCodec("place_on_condition").forGetter(ActionOnBlockPlacePower::getPlaceOnCondition),
            Direction.CODEC.listOf().optionalFieldOf("directions", List.of(Direction.values())).forGetter(ActionOnBlockPlacePower::getDirections),
            ExtraEnumCodecs.HAND.listOf().optionalFieldOf("hands", List.of(InteractionHand.values())).forGetter(ActionOnBlockPlacePower::getHands),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(ActionOnBlockPlacePower::getResultStack),
            ItemAction.optionalCodec("result_item_action").forGetter(ActionOnBlockPlacePower::getResultItemAction)
    ).apply(i, ActionOnBlockPlacePower::new));
    private final EntityAction entityAction;
    private final ItemAction heldItemAction;
    private final BlockAction placeToAction, placeOnAction;
    private final ItemCondition itemCondition;
    private final BlockCondition placeToCondition, placeOnCondition;
    private final List<Direction> directions;
    private final List<InteractionHand> hands;
    private final Optional<ItemStack> resultStack;
    private final ItemAction resultItemAction;

    protected ActionOnBlockPlacePower(BaseSettings settings, EntityAction entityAction, ItemAction heldItemAction, BlockAction placeToAction, BlockAction placeOnAction, ItemCondition itemCondition, BlockCondition placeToCondition, BlockCondition placeOnCondition, List<Direction> directions, List<InteractionHand> hands, Optional<ItemStack> resultStack, ItemAction resultItemAction) {
        super(settings);
        this.entityAction = entityAction;
        this.heldItemAction = heldItemAction;
        this.placeToAction = placeToAction;
        this.placeOnAction = placeOnAction;
        this.itemCondition = itemCondition;
        this.placeToCondition = placeToCondition;
        this.placeOnCondition = placeOnCondition;
        this.directions = directions;
        this.hands = hands;
        this.resultStack = resultStack;
        this.resultItemAction = resultItemAction;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public ItemAction getHeldItemAction() {
        return this.heldItemAction;
    }

    public BlockAction getPlaceToAction() {
        return this.placeToAction;
    }

    public BlockAction getPlaceOnAction() {
        return this.placeOnAction;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public BlockCondition getPlaceToCondition() {
        return this.placeToCondition;
    }

    public BlockCondition getPlaceOnCondition() {
        return this.placeOnCondition;
    }

    public List<Direction> getDirections() {
        return this.directions;
    }

    public List<InteractionHand> getHands() {
        return this.hands;
    }

    public Optional<ItemStack> getResultStack() {
        return this.resultStack;
    }

    public ItemAction getResultItemAction() {
        return this.resultItemAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
