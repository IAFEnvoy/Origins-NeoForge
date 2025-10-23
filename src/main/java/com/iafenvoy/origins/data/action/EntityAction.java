package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public interface EntityAction extends BiConsumer<Level, BlockPos> {
    Codec<EntityAction> CODEC = ActionRegistries.ENTITY_ACTION.byNameCodec().dispatch("type", EntityAction::codec, x -> x);

    MapCodec<? extends EntityAction> codec();

    @Override
    void accept(Level level, BlockPos pos);
}
