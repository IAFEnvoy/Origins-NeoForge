package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

@NotImplementedYet
public class ModelColorPower extends Power {
    public static final MapCodec<ModelColorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.fieldOf("color").forGetter(ModelColorPower::getColor)
    ).apply(i, ModelColorPower::new));
    private final int color;

    public ModelColorPower(BaseSettings settings, int color) {
        super(settings);
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
