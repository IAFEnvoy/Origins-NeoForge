package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum AttachableCondition implements BlockCondition {
    INSTANCE;
    public static final MapCodec<AttachableCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return Arrays.stream(Direction.values()).anyMatch(d -> level.getBlockState(pos.relative(d)).isFaceSturdy(level, pos, d.getOpposite()));
    }
}
