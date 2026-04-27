package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.util.Mutable;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record InteractionPowerSettings(ItemCondition itemCondition, List<InteractionHand> hands,
                                       Optional<ItemStack> resultStack, ItemAction heldItemAction,
                                       ItemAction resultItemAction) {
    public static final MapCodec<InteractionPowerSettings> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.optionalCodec("item_condition").forGetter(InteractionPowerSettings::itemCondition),
            ExtraEnumCodecs.HAND.listOf().optionalFieldOf("hands", List.of(InteractionHand.values())).forGetter(InteractionPowerSettings::hands),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(InteractionPowerSettings::resultStack),
            ItemAction.optionalCodec("held_item_action").forGetter(InteractionPowerSettings::heldItemAction),
            ItemAction.optionalCodec("result_item_action").forGetter(InteractionPowerSettings::resultItemAction)
    ).apply(i, InteractionPowerSettings::new));

    public boolean appliesTo(Level level, InteractionHand hand, ItemStack stack) {
        return this.appliesTo(hand) && this.appliesTo(level, stack);
    }

    public boolean appliesTo(InteractionHand hand) {
        return this.hands().contains(hand);
    }

    public boolean appliesTo(Level level, ItemStack stack) {
        return this.itemCondition().test(level, stack);
    }

    public void performActorItemStuff(LivingEntity actor, InteractionHand hand) {
        Mutable<ItemStack> heldStack = Mutable.access(() -> actor.getItemInHand(hand), stack -> actor.setItemInHand(hand, stack));
        this.heldItemAction().execute(actor.level(), actor, heldStack);
        Mutable<ItemStack> resultingStack = this.resultStack.map(ItemStack::copy).map(Mutable::of).orElse(heldStack);
        this.resultItemAction().execute(actor.level(), actor, resultingStack);
        if (this.resultStack().isPresent())
            if (resultingStack.get().isEmpty())
                actor.setItemInHand(hand, resultingStack.get());
            else if (actor instanceof Player player)
                player.getInventory().placeItemBackInInventory(resultingStack.get());
    }
}
