package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.math.ColorHolder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

@NotImplementedYet
public class ModelColorPower extends Power {
    public static final MapCodec<ModelColorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ColorHolder.CODEC.forGetter(ModelColorPower::getColor)
    ).apply(i, ModelColorPower::new));
    private final ColorHolder color;

    public ModelColorPower(BaseSettings settings, ColorHolder color) {
        super(settings);
        this.color = color;
    }

    public ColorHolder getColor() {
        return this.color;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
