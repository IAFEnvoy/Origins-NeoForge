package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public enum ExposedToSunCondition implements EntityCondition {
    INSTANCE;
    public static final MapCodec<ExposedToSunCondition> CODEC = MapCodec.unit(INSTANCE);
    private static final InRainCondition IN_RAIN = InRainCondition.INSTANCE;
    private static final BrightnessCondition BRIGHTNESS = new BrightnessCondition(Comparison.GREATER_THAN, 0.5F);
    private static final ExposedToSkyCondition EXPOSED_TO_SKY = ExposedToSkyCondition.INSTANCE;

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity.level().isDay()
                && !IN_RAIN.test(entity)
                && BRIGHTNESS.test(entity)
                && EXPOSED_TO_SKY.test(entity);
    }
}
