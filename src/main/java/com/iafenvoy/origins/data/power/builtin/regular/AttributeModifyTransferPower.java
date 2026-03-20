package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AttributeModifyTransferPower(String modifyClass, Optional<ResourceLocation> attribute,
                                           double multiplier) implements Power {
    public static final MapCodec<AttributeModifyTransferPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("class", "").forGetter(AttributeModifyTransferPower::modifyClass),
            ResourceLocation.CODEC.optionalFieldOf("attribute").forGetter(AttributeModifyTransferPower::attribute),
            Codec.DOUBLE.optionalFieldOf("multiplier", 1.0).forGetter(AttributeModifyTransferPower::multiplier)
    ).apply(i, AttributeModifyTransferPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
