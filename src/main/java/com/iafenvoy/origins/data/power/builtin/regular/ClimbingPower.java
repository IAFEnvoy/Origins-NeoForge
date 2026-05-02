package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ClimbingPower extends Power {
    public static final MapCodec<ClimbingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("allow_holding", true).forGetter(ClimbingPower::isAllowHolding),
            EntityCondition.optionalCodec("hold_condition").forGetter(ClimbingPower::getHoldCondition)
    ).apply(i, ClimbingPower::new));
    private final boolean allowHolding;
    private final EntityCondition holdCondition;

    public ClimbingPower(BaseSettings settings, boolean allowHolding, EntityCondition holdCondition) {
        super(settings);
        this.allowHolding = allowHolding;
        this.holdCondition = holdCondition;
    }

    public boolean isAllowHolding() {
        return this.allowHolding;
    }

    public EntityCondition getHoldCondition() {
        return this.holdCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean canHold(Entity entity) {
        return this.allowHolding && this.holdCondition.test(entity);
    }
}
