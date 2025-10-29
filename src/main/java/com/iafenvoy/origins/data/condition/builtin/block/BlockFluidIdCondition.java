package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record BlockFluidIdCondition(FluidCondition fluidCondition) implements BlockCondition {
    public static final MapCodec<BlockFluidIdCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidCondition.CODEC.fieldOf("fluid_condition").forGetter(BlockFluidIdCondition::fluidCondition)
    ).apply(i, BlockFluidIdCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return this.fluidCondition.test(level.getFluidState(pos));
    }
}
