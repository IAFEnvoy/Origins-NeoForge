package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.accessor.MovingEntity;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public enum MovingCondition implements EntityCondition {
    INSTANCE;
    public static final MapCodec<MovingCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return ((MovingEntity) entity).origins$isMoving();
    }
}
