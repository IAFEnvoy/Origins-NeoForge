package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface BlockCondition {
    Codec<BlockCondition> CODEC = ConditionRegistries.BLOCK_CONDITION.byNameCodec().dispatch("type", BlockCondition::codec, x -> x);

    static MapCodec<BlockCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BlockCondition> codec();

    boolean test(@NotNull Level level, @NotNull BlockPos pos);
}
