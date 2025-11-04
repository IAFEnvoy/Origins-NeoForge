package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ClimbingPower(boolean allowHolding, EntityCondition holdCondition) implements Power {
    public static final MapCodec<ClimbingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("allow_holding", true).forGetter(ClimbingPower::allowHolding),
            EntityCondition.optionalCodec("hold_condition").forGetter(ClimbingPower::holdCondition)
    ).apply(i, ClimbingPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean canHold(Entity entity) {
        return this.allowHolding && this.holdCondition.test(entity);
    }
}
