package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public record ModifyValuePower() implements Power {

//    public static MapCodec<ModifyValuePower> CODEC = ListConfiguration.MODIFIER_CODEC
//            .xmap(ModifyValuePower::new, ModifyValuePower::modifiers).codec();

// TODO ListConfiguration

//    public static final Codec<ModifyVelocityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyVelocityConfiguration::modifiers),
//            ExtraCodecs.strictOptionalField(SerializableDataTypes.AXIS_SET, "axes", EnumSet.allOf(Direction.Axis.class)).forGetter(ModifyVelocityConfiguration::axes)
//    ).apply(instance, ModifyVelocityConfiguration::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return null;
    }
}
