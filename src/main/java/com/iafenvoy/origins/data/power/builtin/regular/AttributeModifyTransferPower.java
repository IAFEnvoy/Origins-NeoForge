package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

@NotImplementedYet
public class AttributeModifyTransferPower extends Power {
    public static final MapCodec<AttributeModifyTransferPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.STRING.optionalFieldOf("class", "").forGetter(AttributeModifyTransferPower::getModifyClass),
            ResourceLocation.CODEC.optionalFieldOf("attribute").forGetter(AttributeModifyTransferPower::getAttribute),
            Codec.DOUBLE.optionalFieldOf("multiplier", 1.0).forGetter(AttributeModifyTransferPower::getMultiplier)
    ).apply(i, AttributeModifyTransferPower::new));
    private final String modifyClass;
    private final Optional<ResourceLocation> attribute;
    private final double multiplier;

    public AttributeModifyTransferPower(BaseSettings settings, String modifyClass, Optional<ResourceLocation> attribute, double multiplier) {
        super(settings);
        this.modifyClass = modifyClass;
        this.attribute = attribute;
        this.multiplier = multiplier;
    }

    public String getModifyClass() {
        return this.modifyClass;
    }

    public Optional<ResourceLocation> getAttribute() {
        return this.attribute;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
