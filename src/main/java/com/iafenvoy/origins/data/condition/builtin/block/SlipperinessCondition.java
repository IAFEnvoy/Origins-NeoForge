package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record SlipperinessCondition(Comparison comparison, double compareTo) implements BlockCondition {
    public static final MapCodec<SlipperinessCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(SlipperinessCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(SlipperinessCondition::compareTo)
    ).apply(i, SlipperinessCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return this.comparison.compare(level.getBlockState(pos).getBlock().getFriction(), this.compareTo);
    }
}
