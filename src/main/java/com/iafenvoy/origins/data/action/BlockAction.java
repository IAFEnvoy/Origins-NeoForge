package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BlockAction {
    Codec<BlockAction> CODEC = DefaultedCodec.registryDispatch(ActionRegistries.BLOCK_ACTION, BlockAction::codec, Function.identity(), () -> EmptyAction.INSTANCE);

    static MapCodec<BlockAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BlockAction> codec();

    void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction);
}
