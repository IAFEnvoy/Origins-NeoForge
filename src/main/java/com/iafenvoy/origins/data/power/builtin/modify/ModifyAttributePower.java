package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public record ModifyAttributePower(Holder<Attribute> attribute) implements Power {

    public static final MapCodec<ModifyAttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(ModifyAttributePower::attribute)
    ).apply(i, ModifyAttributePower::new));

// TODO ListConfiguration

//    public static final MapCodec<ModifyAttributeConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
//            SerializableDataTypes.ATTRIBUTE.fieldOf("attribute").forGetter(ModifyAttributeConfiguration::attribute),
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyAttributeConfiguration::modifiers)
//    ).apply(instance, ModifyAttributeConfiguration::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
