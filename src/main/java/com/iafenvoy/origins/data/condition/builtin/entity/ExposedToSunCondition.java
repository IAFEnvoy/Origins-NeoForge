package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.Sided;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;

import org.jetbrains.annotations.NotNull;

public enum ExposedToSunCondition implements EntityCondition, Sided {
    INSTANCE;
    public static final MapCodec<ExposedToSunCondition> CODEC = MapCodec.unit(INSTANCE);
    private static final BrightnessCondition BRIGHTNESS = new BrightnessCondition(new Comparison(Comparison.CompareOperation.GREATER_THAN, 0.5F));
    private static final ExposedToSkyCondition EXPOSED_TO_SKY = ExposedToSkyCondition.INSTANCE;

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public Dist side() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        long relativeDayTime = entity.level().getDayTime() % 24000L;
        return relativeDayTime < 12000 && !entity.isInRain() && BRIGHTNESS.test(entity) && EXPOSED_TO_SKY.test(entity);
    }
}
