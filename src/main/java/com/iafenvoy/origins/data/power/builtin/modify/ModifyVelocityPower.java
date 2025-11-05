package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public record ModifyVelocityPower(Set<Direction.AxisDirection> axes) implements Power {

    public static final MapCodec<ModifyVelocityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraEnumCodecs.AXIS.listOf().fieldOf("axes").forGetter(e -> new ArrayList<>(e.axes()))
    ).apply(i, (e)-> new ModifyVelocityPower(Set.copyOf(e))));

// TODO ListConfiguration

//    public static final Codec<ModifyVelocityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyVelocityConfiguration::modifiers),
//            ExtraCodecs.strictOptionalField(SerializableDataTypes.AXIS_SET, "axes", EnumSet.allOf(Direction.Axis.class)).forGetter(ModifyVelocityConfiguration::axes)
//    ).apply(instance, ModifyVelocityConfiguration::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
