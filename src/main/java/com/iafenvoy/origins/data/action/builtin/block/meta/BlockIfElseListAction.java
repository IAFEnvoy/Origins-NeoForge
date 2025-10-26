package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BlockIfElseListAction(List<ConditionedActionHolder> actions) implements BlockAction {
    public static final MapCodec<BlockIfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(BlockIfElseListAction::actions)
    ).apply(i, BlockIfElseListAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        for (ConditionedActionHolder holder : this.actions)
            if (holder.condition.test(level, pos)) {
                holder.action.execute(level, pos, direction);
                break;
            }
    }

    private record ConditionedActionHolder(BlockCondition condition, BlockAction action) {
        public static final Codec<ConditionedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockCondition.CODEC.fieldOf("condition").forGetter(ConditionedActionHolder::condition),
                BlockAction.CODEC.fieldOf("action").forGetter(ConditionedActionHolder::action)
        ).apply(i, ConditionedActionHolder::new));
    }
}
