package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChoiceAction(List<WeightedActionHolder> actions) implements ItemAction {
    public static final MapCodec<ChoiceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WeightedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(ChoiceAction::actions)
    ).apply(i, ChoiceAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        WeightedActionHolder holder = WeightedRandomSelector.selectRandomByWeight(this.actions);
        if (holder != null) holder.element.execute(level, source, stack);
    }

    private record WeightedActionHolder(ItemAction element, int weight) implements WeightedRandomSelector.WeightGetter {
        public static final Codec<WeightedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                ItemAction.CODEC.fieldOf("element").forGetter(WeightedActionHolder::element),
                Codec.INT.optionalFieldOf("weight", 0).forGetter(WeightedActionHolder::weight)
        ).apply(i, WeightedActionHolder::new));
    }
}
