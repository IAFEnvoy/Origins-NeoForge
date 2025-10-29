package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum RidingCondition implements BiEntityCondition {
    INSTANCE;
    public static final MapCodec<RidingCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return Objects.equals(source.getVehicle(), target);
    }
}
