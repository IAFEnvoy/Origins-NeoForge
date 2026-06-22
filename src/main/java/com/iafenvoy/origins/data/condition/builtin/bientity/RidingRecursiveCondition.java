package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum RidingRecursiveCondition implements BiEntityCondition {
    INSTANCE;
    public static final MapCodec<RidingRecursiveCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        if (source.getVehicle() == null) return false;
        Entity vehicle = source.getVehicle();
        while (vehicle != target && vehicle != null)
            vehicle = vehicle.getVehicle();
        return Objects.equals(vehicle, target);
    }
}
