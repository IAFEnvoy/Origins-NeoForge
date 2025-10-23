package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record SetBlockAction(Block block) implements BlockAction {
    public static final MapCodec<SetBlockAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(SetBlockAction::block)
    ).apply(i, SetBlockAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        level.setBlock(pos, this.block.defaultBlockState(), Block.UPDATE_ALL);
    }
}
