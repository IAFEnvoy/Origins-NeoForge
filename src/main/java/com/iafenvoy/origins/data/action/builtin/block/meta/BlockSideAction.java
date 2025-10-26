package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforgespi.Environment;
import org.jetbrains.annotations.NotNull;

public record BlockSideAction(BlockAction action, Dist side) implements BlockAction {
    public static final MapCodec<BlockSideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockAction.CODEC.fieldOf("action").forGetter(BlockSideAction::action),
            ExtraEnumCodecs.DIST.fieldOf("side").forGetter(BlockSideAction::side)
    ).apply(i, BlockSideAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        if (Environment.get().getDist() == this.side) this.action.execute(level, pos, direction);
    }
}
