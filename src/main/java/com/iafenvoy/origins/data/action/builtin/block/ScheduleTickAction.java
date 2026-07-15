package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ScheduleTickAction(int delay) implements BlockAction {
    public static final MapCodec<ScheduleTickAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("delay").forGetter(ScheduleTickAction::delay)
    ).apply(instance, ScheduleTickAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
        level.scheduleTick(pos, level.getBlockState(pos).getBlock(), this.delay);
    }
}
