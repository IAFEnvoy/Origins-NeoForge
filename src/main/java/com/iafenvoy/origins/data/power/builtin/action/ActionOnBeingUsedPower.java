package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record ActionOnBeingUsedPower(BiEntityAction biEntityAction, ItemAction heldItemAction,
                                     ItemAction resultItemAction, BiEntityCondition biEntityCondition,
                                     ItemCondition itemCondition, List<InteractionHand> hands,
                                     Optional<ItemStack> resultStack, InteractionResult interactionResult,
                                     int priority) implements Power {
    public static final MapCodec<ActionOnBeingUsedPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.optionalCodec("bientity_action").forGetter(ActionOnBeingUsedPower::biEntityAction),
            ItemAction.optionalCodec("held_item_action").forGetter(ActionOnBeingUsedPower::heldItemAction),
            ItemAction.optionalCodec("result_item_action").forGetter(ActionOnBeingUsedPower::resultItemAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnBeingUsedPower::biEntityCondition),
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnBeingUsedPower::itemCondition),
            CombinedCodecs.HAND.optionalFieldOf("hands", List.of(InteractionHand.values())).forGetter(ActionOnBeingUsedPower::hands),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(ActionOnBeingUsedPower::resultStack),
            ExtraEnumCodecs.INTERACTION_RESULT.optionalFieldOf("interaction_result", InteractionResult.SUCCESS).forGetter(ActionOnBeingUsedPower::interactionResult),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ActionOnBeingUsedPower::priority)
    ).apply(i, ActionOnBeingUsedPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
