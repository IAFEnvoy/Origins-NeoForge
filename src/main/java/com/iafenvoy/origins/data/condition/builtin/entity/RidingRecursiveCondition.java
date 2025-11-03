package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record RidingRecursiveCondition(BiEntityCondition biEntityCondition, Comparison comparison,
                                       int compareTo) implements EntityCondition {
    public static final MapCodec<RidingRecursiveCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(RidingRecursiveCondition::biEntityCondition),
            Comparison.CODEC.fieldOf("comparison").forGetter(RidingRecursiveCondition::comparison),
            Codec.INT.fieldOf("compare_to").forGetter(RidingRecursiveCondition::compareTo)
    ).apply(i, RidingRecursiveCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        Entity vehicle = entity.getVehicle();
        int matches = 0;
        while (vehicle != null) {
            final Entity finalVehicle = vehicle;
            if (this.biEntityCondition.test(entity, finalVehicle)) ++matches;
            vehicle = vehicle.getVehicle();
        }
        return this.comparison.compare(matches, this.compareTo);
    }
}
