package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record BlockActionAtAction(BlockAction blockAction) implements EntityAction {
    public static final MapCodec<BlockActionAtAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockAction.optionalCodec("block_action").forGetter(BlockActionAtAction::blockAction)
    ).apply(i, BlockActionAtAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        BlockPos pos = source.blockPosition();
        this.blockAction.execute(source.level(), pos, Direction.UP);
    }
}
