package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record BlockPlaceSettings(EntityAction entityAction, ItemAction heldItemAction, BlockAction placeToAction,
                                 BlockAction placeOnAction, ItemCondition itemCondition,
                                 BlockCondition placeToCondition, BlockCondition placeOnCondition,
                                 List<Direction> directions, List<InteractionHand> hands,
                                 Optional<ItemStack> resultStack, ItemAction resultItemAction) {
    public static final MapCodec<BlockPlaceSettings> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("entity_action").forGetter(BlockPlaceSettings::entityAction),
            ItemAction.optionalCodec("held_item_action").forGetter(BlockPlaceSettings::heldItemAction),
            BlockAction.optionalCodec("place_to_action").forGetter(BlockPlaceSettings::placeToAction),
            BlockAction.optionalCodec("place_on_action").forGetter(BlockPlaceSettings::placeOnAction),
            ItemCondition.optionalCodec("item_condition").forGetter(BlockPlaceSettings::itemCondition),
            BlockCondition.optionalCodec("place_to_condition").forGetter(BlockPlaceSettings::placeToCondition),
            BlockCondition.optionalCodec("place_on_condition").forGetter(BlockPlaceSettings::placeOnCondition),
            Direction.CODEC.listOf().optionalFieldOf("directions", List.of(Direction.values())).forGetter(BlockPlaceSettings::directions),
            ExtraEnumCodecs.HAND.listOf().optionalFieldOf("hands", List.of(InteractionHand.values())).forGetter(BlockPlaceSettings::hands),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(BlockPlaceSettings::resultStack),
            ItemAction.optionalCodec("result_item_action").forGetter(BlockPlaceSettings::resultItemAction)
    ).apply(i, BlockPlaceSettings::new));
}
