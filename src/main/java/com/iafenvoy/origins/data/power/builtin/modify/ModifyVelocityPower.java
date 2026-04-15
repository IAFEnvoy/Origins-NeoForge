package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.math.Modifier;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ModifyVelocityPower(List<Modifier> modifiers, Set<Direction.AxisDirection> axes) implements Power {

    public static final MapCodec<ModifyVelocityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyVelocityPower::modifiers),
            ExtraEnumCodecs.AXIS.listOf().fieldOf("axis").forGetter(e -> new ArrayList<>(e.axes()))
    ).apply(i, (m, e) -> new ModifyVelocityPower(m, Set.copyOf(e))));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
