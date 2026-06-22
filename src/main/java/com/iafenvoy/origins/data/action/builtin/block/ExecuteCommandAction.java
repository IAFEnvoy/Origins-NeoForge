package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data._common.helper.CommandHelper;
import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ExecuteCommandAction(String command) implements BlockAction, CommandHelper {
    public static final MapCodec<ExecuteCommandAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(ExecuteCommandAction::command)
    ).apply(i, ExecuteCommandAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
        this.executeCommand(level, stack -> stack.withPosition(pos.getCenter()), this.command);
    }
}
