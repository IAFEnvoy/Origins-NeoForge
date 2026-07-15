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

public record DestroyAction(boolean drop) implements BlockAction {
    public static final MapCodec<DestroyAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("drop", true).forGetter(DestroyAction::drop)
    ).apply(instance, DestroyAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
        level.destroyBlock(pos, this.drop);
    }
}
