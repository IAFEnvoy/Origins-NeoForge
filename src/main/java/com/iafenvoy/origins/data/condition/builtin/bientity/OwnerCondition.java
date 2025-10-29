package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum OwnerCondition implements BiEntityCondition {
    INSTANCE;
    public static final MapCodec<OwnerCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return target instanceof OwnableEntity ownable && Objects.equals(ownable.getOwnerUUID(), source.getUUID());
    }
}
