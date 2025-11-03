package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record FallDistanceCondition(Comparison comparison, double compareTo) implements EntityCondition {
    public static final MapCodec<FallDistanceCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(FallDistanceCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(FallDistanceCondition::compareTo)
    ).apply(i, FallDistanceCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.comparison.compare(entity.fallDistance, this.compareTo);
    }
}
