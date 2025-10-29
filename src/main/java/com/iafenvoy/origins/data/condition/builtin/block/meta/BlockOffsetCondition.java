package com.iafenvoy.origins.data.condition.builtin.block.meta;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record BlockOffsetCondition(BlockCondition condition, int x, int y, int z) implements BlockCondition {
    public static final MapCodec<BlockOffsetCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.CODEC.fieldOf("condition").forGetter(BlockOffsetCondition::condition),
            Codec.INT.optionalFieldOf("x", 0).forGetter(BlockOffsetCondition::x),
            Codec.INT.optionalFieldOf("y", 0).forGetter(BlockOffsetCondition::y),
            Codec.INT.optionalFieldOf("z", 0).forGetter(BlockOffsetCondition::z)
    ).apply(i, BlockOffsetCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return this.condition.test(level, pos.offset(this.x, this.y, this.z));
    }
}
