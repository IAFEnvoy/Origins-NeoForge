package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.InteractionPowerSettings;
import com.iafenvoy.origins.data._common.helper.InteractionPowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PreventEntityUsePower extends Power implements InteractionPowerHelper {
    public static final MapCodec<PreventEntityUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            InteractionPowerSettings.CODEC.forGetter(PreventEntityUsePower::getInteractionSettings),
            BiEntityAction.optionalCodec("bientity_action").forGetter(PreventEntityUsePower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(PreventEntityUsePower::getBiEntityCondition)
    ).apply(i, PreventEntityUsePower::new));
    private final InteractionPowerSettings interactionSettings;
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;

    public PreventEntityUsePower(BaseSettings settings, InteractionPowerSettings interactionSettings, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition) {
        super(settings);
        this.interactionSettings = interactionSettings;
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
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
        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static Optional<InteractionResult> tryPrevent(Entity self, Entity other, InteractionHand hand) {
        for (PreventEntityUsePower power : OriginDataHolder.get(self).streamActivePowers(PreventEntityUsePower.class).toList()) {
            Optional<InteractionResult> result = power.tryExecute(self, other, hand);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }
}
