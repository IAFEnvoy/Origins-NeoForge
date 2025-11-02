package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.Shape;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record BlockInRadiusCondition(BlockCondition blockCondition, int radius, Shape shape, Comparison comparison,
                                     int compareTo) implements EntityCondition {
    public static final MapCodec<BlockInRadiusCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlockInRadiusCondition::blockCondition),
            Codec.INT.fieldOf("radius").forGetter(BlockInRadiusCondition::radius),
            Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(BlockInRadiusCondition::shape),
            Comparison.CODEC.optionalFieldOf("comparison", Comparison.GREATER_THAN_OR_EQUAL).forGetter(BlockInRadiusCondition::comparison),
            Codec.INT.optionalFieldOf("compare_to", 1).forGetter(BlockInRadiusCondition::compareTo)
    ).apply(i, BlockInRadiusCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        int matches = 0;
        for (BlockPos pos : this.shape.getBlocks(entity.blockPosition(), this.radius))
            if (this.blockCondition.test(entity.level(), pos))
                ++matches;
        return this.comparison.compare(matches, this.compareTo);
    }
}
