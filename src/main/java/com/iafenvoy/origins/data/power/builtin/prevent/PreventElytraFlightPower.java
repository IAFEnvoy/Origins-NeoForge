package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

@NotImplementedYet
public class PreventElytraFlightPower extends Power {
    public static final MapCodec<PreventElytraFlightPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action").forGetter(PreventElytraFlightPower::getEntityAction)
    ).apply(i, PreventElytraFlightPower::new));
    private final EntityAction entityAction;

    protected PreventElytraFlightPower(BaseSettings settings, EntityAction entityAction) {
        super(settings);
        this.entityAction = entityAction;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
