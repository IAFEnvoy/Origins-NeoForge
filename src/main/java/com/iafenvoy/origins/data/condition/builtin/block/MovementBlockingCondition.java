package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public enum MovementBlockingCondition implements BlockCondition {
    INSTANCE;
    public static final MapCodec<MovementBlockingCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.blocksMotion() && !state.getCollisionShape(level, pos).isEmpty();
    }
}
