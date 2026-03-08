package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PreventGameEventPower(Optional<ResourceLocation> event, String eventTag,
                                    EntityCondition condition) implements Power {
    public static final MapCodec<PreventGameEventPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.optionalFieldOf("event").forGetter(PreventGameEventPower::event),
            Codec.STRING.optionalFieldOf("tag", "").forGetter(PreventGameEventPower::eventTag),
            EntityCondition.optionalCodec("condition").forGetter(PreventGameEventPower::condition)
    ).apply(i, PreventGameEventPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
