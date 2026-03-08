package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record PhasingPower(boolean blacklist, String renderType, float viewDistance,
                           BlockCondition blockCondition, EntityCondition phaseDownCondition,
                           EntityCondition condition) implements Power {
    public static final MapCodec<PhasingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("blacklist", false).forGetter(PhasingPower::blacklist),
            Codec.STRING.optionalFieldOf("render_type", "none").forGetter(PhasingPower::renderType),
            Codec.FLOAT.optionalFieldOf("view_distance", 10F).forGetter(PhasingPower::viewDistance),
            BlockCondition.optionalCodec("block_condition").forGetter(PhasingPower::blockCondition),
            EntityCondition.optionalCodec("phase_down_condition").forGetter(PhasingPower::phaseDownCondition),
            EntityCondition.optionalCodec("condition").forGetter(PhasingPower::condition)
    ).apply(i, PhasingPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
