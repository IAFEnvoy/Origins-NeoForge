package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data._common.InteractionPowerSettings;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.helper.InteractionPowerHelper;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.NotNull;

//TODO::Merge with ActionOnEntityUsePower
public class ActionOnBeingUsedPower extends Power implements InteractionPowerHelper, Prioritized {
    public static final MapCodec<ActionOnBeingUsedPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            InteractionPowerSettings.CODEC.forGetter(ActionOnBeingUsedPower::getInteractionSettings),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ActionOnBeingUsedPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnBeingUsedPower::getBiEntityCondition),
            ExtraEnumCodecs.INTERACTION_RESULT.optionalFieldOf("interaction_result", InteractionResult.SUCCESS).forGetter(ActionOnBeingUsedPower::getInteractionResult),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ActionOnBeingUsedPower::getPriority)
    ).apply(i, ActionOnBeingUsedPower::new));
    private final InteractionPowerSettings interactionSettings;
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;
    private final InteractionResult interactionResult;
    private final int priority;

    public ActionOnBeingUsedPower(BaseSettings settings, InteractionPowerSettings interactionSettings, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, InteractionResult interactionResult, int priority) {
        super(settings);
        this.interactionSettings = interactionSettings;
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.interactionResult = interactionResult;
        this.priority = priority;
    }

    @Override
    public InteractionPowerSettings getInteractionSettings() {
        return this.interactionSettings;
    }

    @Override
    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    @Override
    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    @Override
    public InteractionResult getInteractionResult() {
        return this.interactionResult;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
