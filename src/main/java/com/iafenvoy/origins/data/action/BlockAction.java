package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

public interface BlockAction extends TriConsumer<Level, BlockPos, Direction> {
    Codec<BlockAction> CODEC = ActionRegistries.BLOCK_ACTION.byNameCodec().dispatch("type", BlockAction::codec, x -> x);

    @NotNull
    MapCodec<? extends BlockAction> codec();

    @Override
    void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction);
}
