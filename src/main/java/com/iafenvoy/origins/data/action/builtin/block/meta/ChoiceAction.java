package com.iafenvoy.origins.data.action.builtin.block.meta;

import com.iafenvoy.origins.data._common.WeightedActionEntry;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record ChoiceAction(List<WeightedActionEntry<BlockAction>> actions) implements BlockAction {
    public static final MapCodec<ChoiceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WeightedActionEntry.codec(BlockAction.CODEC).listOf().fieldOf("actions").forGetter(ChoiceAction::actions)
    ).apply(i, ChoiceAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
        WeightedActionEntry<BlockAction> holder = WeightedRandomSelector.selectRandomByWeight(this.actions);
        if (holder != null) holder.element().execute(level, pos, direction);
    }
}
