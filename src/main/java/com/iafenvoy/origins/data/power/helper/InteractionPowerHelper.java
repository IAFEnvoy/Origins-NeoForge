package com.iafenvoy.origins.data.power.helper;

import com.iafenvoy.origins.data._common.InteractionPowerSettings;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface InteractionPowerHelper {
    InteractionPowerSettings getInteractionSettings();

    BiEntityAction getBiEntityAction();

    BiEntityCondition getBiEntityCondition();

    InteractionResult getInteractionResult();

    default Optional<InteractionResult> tryExecute(Entity self, Entity other, InteractionHand hand) {
        if (other instanceof LivingEntity living && this.check(other, self, hand, living.getItemInHand(hand)))
            return Optional.of(this.executeAction(other, self, hand));
        return Optional.empty();
    }

    default boolean check(Entity actor, Entity target, InteractionHand hand, ItemStack held) {
        return this.getInteractionSettings().appliesTo(actor.level(), hand, held) && this.getBiEntityCondition().test(actor, target);
    }

    default InteractionResult executeAction(Entity actor, Entity target, InteractionHand hand) {
        this.getBiEntityAction().execute(actor, target);
        if (actor instanceof LivingEntity living) this.getInteractionSettings().performActorItemStuff(living, hand);
        return this.getInteractionResult();
    }
}
