package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyExhaustionPower(List<Modifier> modifiers,
                                    EntityCondition condition) implements Power {
    public static final MapCodec<ModifyExhaustionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyExhaustionPower::modifiers),
            EntityCondition.optionalCodec("condition").forGetter(ModifyExhaustionPower::condition)
    ).apply(i, ModifyExhaustionPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
