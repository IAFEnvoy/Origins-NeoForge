package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record DistanceCondition(Comparison comparison, double compareTo) implements BiEntityCondition {
    public static final MapCodec<DistanceCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(DistanceCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(DistanceCondition::compareTo)
    ).apply(i, DistanceCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return this.comparison.compare(source.distanceTo(target), this.compareTo);
    }
}
