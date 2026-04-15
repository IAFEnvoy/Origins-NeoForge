package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class TogglePower extends Power {
    public static final MapCodec<TogglePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("active_by_default", true).forGetter(togglePower -> togglePower.isActiveByDefault()),
            Codec.BOOL.optionalFieldOf("retain_state", true).forGetter(togglePower -> togglePower.isRetainState()),
            EntityCondition.optionalCodec("condition").forGetter(togglePower -> togglePower.getCondition())
    ).apply(i, TogglePower::new));
    private final boolean activeByDefault;
    private final boolean retainState;
    private final EntityCondition condition;

    public TogglePower(BaseSettings settings, boolean activeByDefault, boolean retainState, EntityCondition condition) {
        super(settings);
        this.activeByDefault = activeByDefault;
        this.retainState = retainState;
        this.condition = condition;
    }

    public boolean isActiveByDefault() {
        return this.activeByDefault;
    }

    public boolean isRetainState() {
        return this.retainState;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
