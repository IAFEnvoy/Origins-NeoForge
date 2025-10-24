package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AbsoluteOffsetAction(BlockAction action, int x, int y, int z) implements BlockAction {
    public static final MapCodec<AbsoluteOffsetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockAction.CODEC.fieldOf("event").forGetter(AbsoluteOffsetAction::action),
            Codec.INT.optionalFieldOf("x", 0).forGetter(AbsoluteOffsetAction::x),
            Codec.INT.optionalFieldOf("y", 0).forGetter(AbsoluteOffsetAction::y),
            Codec.INT.optionalFieldOf("z", 0).forGetter(AbsoluteOffsetAction::z)
    ).apply(i, AbsoluteOffsetAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        this.action.accept(level, pos.offset(this.x, this.y, this.z), direction);
    }
}
