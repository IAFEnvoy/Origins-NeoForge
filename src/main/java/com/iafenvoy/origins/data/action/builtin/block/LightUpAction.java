package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class LightUpAction implements BlockAction {
    public static final MapCodec<LightUpAction> CODEC = MapCodec.unit(new LightUpAction());

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(BlockStateProperties.LIT) && !state.getValue(BlockStateProperties.LIT))
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 3);
    }
}
