package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface BlockAction {
    Codec<BlockAction> CODEC = ActionRegistries.BLOCK_ACTION.byNameCodec().dispatch("type", BlockAction::codec, x -> x);

    static MapCodec<BlockAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BlockAction> codec();

    void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction);
}
