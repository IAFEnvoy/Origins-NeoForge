package com.iafenvoy.origins.data.condition.builtin.block.meta;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record OrCondition(List<BlockCondition> conditions) implements BlockCondition {
    public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::conditions)
    ).apply(i, OrCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return this.conditions.stream().anyMatch(x -> x.test(level, pos));
    }
}
