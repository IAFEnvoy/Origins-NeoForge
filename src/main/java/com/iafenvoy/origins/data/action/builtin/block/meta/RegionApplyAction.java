package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.Shape;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RegionApplyAction(int radius, Shape shape, BlockAction blockAction,
                                BlockCondition blockCondition) implements BlockAction {
    public static final MapCodec<RegionApplyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("radius", 16).forGetter(RegionApplyAction::radius),
            Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(RegionApplyAction::shape),
            BlockAction.CODEC.fieldOf("block_action").forGetter(RegionApplyAction::blockAction),
            BlockCondition.optionalCodec("block_condition").forGetter(RegionApplyAction::blockCondition)
    ).apply(i, RegionApplyAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        List<BlockPos> positions = this.shape.getBlocks(pos, this.radius);
        positions.removeIf(p -> !this.blockCondition.test(level, p));
        positions.forEach(x -> this.blockAction.execute(level, x, direction));
    }
}
