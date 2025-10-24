package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BlockChanceAction(BlockAction action, float chance,
                                Optional<BlockAction> failAction) implements BlockAction {
    public static final MapCodec<BlockChanceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockAction.CODEC.fieldOf("action").forGetter(BlockChanceAction::action),
            Codec.FLOAT.fieldOf("chance").forGetter(BlockChanceAction::chance),
            BlockAction.CODEC.optionalFieldOf("fail_action").forGetter(BlockChanceAction::failAction)
    ).apply(i, BlockChanceAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        if (Math.random() < this.chance) this.action.accept(level, pos, direction);
        else this.failAction.ifPresent(x -> x.accept(level, pos, direction));
    }
}
