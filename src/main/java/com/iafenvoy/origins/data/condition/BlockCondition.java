package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface BlockCondition extends BiPredicate<Level, BlockPos> {
    Codec<BlockCondition> CODEC = ConditionRegistries.BLOCK_CONDITION.byNameCodec().dispatch("type", BlockCondition::codec, x -> x);

    @NotNull
    MapCodec<? extends BlockCondition> codec();

    @Override
    boolean test(@NotNull Level level, @NotNull BlockPos pos);
}
