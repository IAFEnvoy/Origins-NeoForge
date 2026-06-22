package com.iafenvoy.origins.data.condition.builtin.block.meta;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record OffsetCondition(BlockCondition condition, int x, int y, int z) implements BlockCondition {
    public static final MapCodec<OffsetCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.CODEC.fieldOf("condition").forGetter(OffsetCondition::condition),
            Codec.INT.optionalFieldOf("x", 0).forGetter(OffsetCondition::x),
            Codec.INT.optionalFieldOf("y", 0).forGetter(OffsetCondition::y),
            Codec.INT.optionalFieldOf("z", 0).forGetter(OffsetCondition::z)
    ).apply(i, OffsetCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return this.condition.test(level, pos.offset(this.x, this.y, this.z));
    }
}
