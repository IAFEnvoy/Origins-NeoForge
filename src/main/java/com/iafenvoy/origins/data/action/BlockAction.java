package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public interface BlockAction extends BiConsumer<Level, BlockPos> {
    Codec<BlockAction> CODEC = ActionRegistries.BLOCK_ACTION.byNameCodec().dispatch("type", BlockAction::codec, x -> x);

    MapCodec<? extends BlockAction> codec();

    @Override
    void accept(Level level, BlockPos pos);
}
