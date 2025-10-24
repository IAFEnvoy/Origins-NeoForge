package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BlockIfElseAction(BlockCondition condition, BlockAction ifAction,
                                Optional<BlockAction> elseAction) implements BlockAction {
    public static final MapCodec<BlockIfElseAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.CODEC.fieldOf("condition").forGetter(BlockIfElseAction::condition),
            BlockAction.CODEC.fieldOf("if_action").forGetter(BlockIfElseAction::ifAction),
            BlockAction.CODEC.optionalFieldOf("else_action").forGetter(BlockIfElseAction::elseAction)
    ).apply(i, BlockIfElseAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        if (this.condition.test(level, pos)) this.ifAction.accept(level, pos, direction);
        else this.elseAction.ifPresent(x -> x.accept(level, pos, direction));
    }
}
