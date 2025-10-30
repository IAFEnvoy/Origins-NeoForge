package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.NotNull;

public enum TamedCondition implements EntityCondition {
    INSTANCE;
    public static final MapCodec<TamedCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null;
    }
}
