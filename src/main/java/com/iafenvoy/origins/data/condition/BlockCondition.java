package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BlockCondition {
    Codec<BlockCondition> CODEC = DefaultedCodec.registryDispatch(ConditionRegistries.BLOCK_CONDITION, BlockCondition::codec, Function.identity(), () -> AlwaysTrueCondition.INSTANCE);

    static MapCodec<BlockCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, AlwaysTrueCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BlockCondition> codec();

    boolean test(@NotNull Level level, @NotNull BlockPos pos);
}
