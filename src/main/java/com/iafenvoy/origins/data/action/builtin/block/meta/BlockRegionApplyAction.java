package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

public record BlockRegionApplyAction(int radius, Shape shape, BlockAction blockAction,
                                     Optional<BlockCondition> blockCondition) implements BlockAction {
    public static final MapCodec<BlockRegionApplyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("radius", 16).forGetter(BlockRegionApplyAction::radius),
            Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(BlockRegionApplyAction::shape),
            BlockAction.CODEC.fieldOf("block_action").forGetter(BlockRegionApplyAction::blockAction),
            BlockCondition.CODEC.optionalFieldOf("block_condition").forGetter(BlockRegionApplyAction::blockCondition)
    ).apply(i, BlockRegionApplyAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        List<BlockPos> positions = this.shape.getProcessor().apply(pos, this.radius);
        this.blockCondition.ifPresent(x -> positions.removeIf(p -> !x.test(level, p)));
        positions.forEach(x -> this.blockAction.accept(level, x, direction));
    }

    //FIXME::Share enum
    public enum Shape implements StringRepresentable {
        CUBE((center, radius) -> {
            ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
            for (int i = -radius; i <= radius; i++)
                for (int j = -radius; j <= radius; j++)
                    for (int k = -radius; k <= radius; k++)
                        builder.add(center.offset(i, j, k));
            return builder.build();
        }),
        STAR((center, radius) -> {
            ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
            for (int i = -radius; i <= radius; i++)
                for (int j = -radius; j <= radius; j++)
                    for (int k = -radius; k <= radius; k++)
                        if (Math.abs(i) + Math.abs(j) + Math.abs(k) <= radius)
                            builder.add(center.offset(i, j, k));
            return builder.build();
        }),
        SPHERE((center, radius) -> {
            ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
            for (int i = -radius; i <= radius; i++)
                for (int j = -radius; j <= radius; j++)
                    for (int k = -radius; k <= radius; k++)
                        if (i * i + j * j + k * k <= radius * radius)
                            builder.add(center.offset(i, j, k));
            return builder.build();
        });
        public static final Codec<Shape> CODEC = StringRepresentable.fromEnum(Shape::values);
        private final BiFunction<BlockPos, Integer, List<BlockPos>> processor;

        Shape(BiFunction<BlockPos, Integer, List<BlockPos>> processor) {
            this.processor = processor;
        }

        public BiFunction<BlockPos, Integer, List<BlockPos>> getProcessor() {
            return this.processor;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
