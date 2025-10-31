package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record EntitySetPower(BiEntityAction actionOnAdd, BiEntityAction actionOnRemove) implements Power {
    public static final MapCodec<EntitySetPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.optionalCodec("action_on_add").forGetter(EntitySetPower::actionOnAdd),
            BiEntityAction.optionalCodec("action_on_remove").forGetter(EntitySetPower::actionOnRemove)
    ).apply(i, EntitySetPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
