package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AdjacentCondition(BlockCondition adjacentCondition, Comparison comparison,
                                double compareTo) implements BlockCondition {
    public static final MapCodec<AdjacentCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.CODEC.fieldOf("adjacent_condition").forGetter(AdjacentCondition::adjacentCondition),
            Comparison.CODEC.fieldOf("comparison").forGetter(AdjacentCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(AdjacentCondition::compareTo)
    ).apply(i, AdjacentCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        int matches = 0;
        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = pos.relative(direction);
            if (level.hasChunkAt(offsetPos) && this.adjacentCondition.test(level, offsetPos)) matches++;
        }
        return this.comparison.compare(matches, this.compareTo);
    }
}
