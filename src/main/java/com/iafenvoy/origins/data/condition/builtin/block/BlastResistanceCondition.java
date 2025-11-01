package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record BlastResistanceCondition(Comparison comparison, double compareTo) implements BlockCondition {
    public static final MapCodec<BlastResistanceCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(BlastResistanceCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(BlastResistanceCondition::compareTo)
    ).apply(i, BlastResistanceCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return this.comparison.compare(level.getBlockState(pos).getBlock().getExplosionResistance(), this.compareTo);
    }
}
