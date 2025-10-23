package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public interface BlockAction extends BiConsumer<Level, BlockPos> {
    Codec<BlockAction> CODEC = ActionRegistries.BLOCK_ACTION.byNameCodec().dispatch("type", BlockAction::type, ActionType::codec);

    ActionType<BlockAction> type();

    @Override
    void accept(Level level, BlockPos pos);
}
