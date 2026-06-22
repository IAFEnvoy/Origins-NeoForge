package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModelColorPower extends Power {
    public static final MapCodec<ModelColorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ColorSettings.CODEC.forGetter(ModelColorPower::getColor)
    ).apply(i, ModelColorPower::new));
    private final ColorSettings color;

    public ModelColorPower(BaseSettings settings, ColorSettings color) {
        super(settings);
        this.color = color;
    }

    public ColorSettings getColor() {
        return this.color;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static Optional<ColorSettings> getColor(Entity entity) {
        return OriginDataHolder.get(entity).streamActivePowers(ModelColorPower.class).map(ModelColorPower::getColor).reduce(ColorSettings::merge);
    }
}
