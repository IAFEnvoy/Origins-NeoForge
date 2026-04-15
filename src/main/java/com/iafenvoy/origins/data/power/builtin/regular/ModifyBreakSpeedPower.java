package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyBreakSpeedPower(List<Modifier> modifiers,
                                    BlockCondition blockCondition,
                                    EntityCondition condition) implements Power {
    public static final MapCodec<ModifyBreakSpeedPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyBreakSpeedPower::modifiers),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyBreakSpeedPower::blockCondition),
            EntityCondition.optionalCodec("condition").forGetter(ModifyBreakSpeedPower::condition)
    ).apply(i, ModifyBreakSpeedPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
