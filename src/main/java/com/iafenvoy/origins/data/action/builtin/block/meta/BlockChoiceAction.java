package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BlockChoiceAction(List<WeightedActionHolder> actions) implements BlockAction {
    public static final MapCodec<BlockChoiceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WeightedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(BlockChoiceAction::actions)
    ).apply(i, BlockChoiceAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        WeightedActionHolder holder = WeightedRandomSelector.selectRandomByWeight(this.actions);
        if (holder != null) holder.element.accept(level, pos, direction);
    }

    private record WeightedActionHolder(BlockAction element,
                                        int weight) implements WeightedRandomSelector.WeightGetter {
        public static final Codec<WeightedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockAction.CODEC.fieldOf("element").forGetter(WeightedActionHolder::element),
                Codec.INT.optionalFieldOf("weight", 0).forGetter(WeightedActionHolder::weight)
        ).apply(i, WeightedActionHolder::new));
    }
}
