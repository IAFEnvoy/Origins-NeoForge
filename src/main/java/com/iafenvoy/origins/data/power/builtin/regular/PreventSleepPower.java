package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PreventSleepPower(BlockCondition blockCondition, Optional<Component> message,
                                EntityCondition condition) implements Power {
    public static final MapCodec<PreventSleepPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(PreventSleepPower::blockCondition),
            ComponentSerialization.CODEC.optionalFieldOf("message").forGetter(PreventSleepPower::message),
            EntityCondition.optionalCodec("condition").forGetter(PreventSleepPower::condition)
    ).apply(i, PreventSleepPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
