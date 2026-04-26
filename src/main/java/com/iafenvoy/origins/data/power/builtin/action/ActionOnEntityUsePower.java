package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.common.ActionInteractionSettings;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.util.MiscUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnEntityUsePower extends Power implements Prioritized {
    public static final MapCodec<ActionOnEntityUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ActionInteractionSettings.CODEC.forGetter(ActionOnEntityUsePower::getInteractionSettings),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ActionOnEntityUsePower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnEntityUsePower::getBiEntityCondition),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ActionOnEntityUsePower::getPriority)
    ).apply(i, ActionOnEntityUsePower::new));
    private final ActionInteractionSettings interactionSettings;
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;
    private final int priority;

    protected ActionOnEntityUsePower(BaseSettings settings, ActionInteractionSettings interactionSettings, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, int priority) {
        super(settings);
        this.interactionSettings = interactionSettings;
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.priority = priority;
    }

    public ActionInteractionSettings getInteractionSettings() {
        return this.interactionSettings;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static Optional<InteractionResult> tryPrevent(Entity self, Entity other, InteractionHand hand) {
        for (ActionOnEntityUsePower power : OriginDataHolder.get(self).streamActivePowers(ActionOnEntityUsePower.class).toList()) {
            Optional<InteractionResult> result = power.tryExecute(self, other, hand);
            if (result.isPresent())
                return result;
        }
        return Optional.empty();
    }

    public static Optional<InteractionResult> tryInteract(Entity self, Entity other, InteractionHand hand) {
        return OriginDataHolder.get(self).streamActivePowers(ActionOnEntityUsePower.class).flatMap(x -> x.tryExecute(self, other, hand).stream()).reduce(MiscUtil::reduce);
    }

    public Optional<InteractionResult> tryExecute(Entity self, Entity other, InteractionHand hand) {
        if (self instanceof LivingEntity living && this.check(self, other, hand, living.getItemInHand(hand))) {
            return Optional.of(this.executeAction(self, other, hand));
        }
        return Optional.empty();
    }

    public boolean check(Entity actor, Entity target, InteractionHand hand, ItemStack held) {
        return this.interactionSettings.appliesTo(actor.level(), hand, held) && this.biEntityCondition.test(actor, target);
    }

    public InteractionResult executeAction(Entity actor, Entity target, InteractionHand hand) {
        this.biEntityAction.execute(actor, target);
        if (actor instanceof LivingEntity living)
            this.interactionSettings.performActorItemStuff(living, hand);
        return this.interactionSettings.actionResult();
    }
}
