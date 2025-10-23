package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiPredicate;

public interface BlockCondition extends BiPredicate<Level, BlockPos> {
    Codec<BlockCondition> CODEC = ConditionRegistries.BLOCK_CONDITION.byNameCodec().dispatch("type", BlockCondition::type, ConditionType::codec);

    ConditionType<BlockCondition> type();

    @Override
    boolean test(Level level, BlockPos pos);
}
