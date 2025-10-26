package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EntityChoiceAction(List<WeightedActionHolder> actions) implements BiEntityAction {
    public static final MapCodec<EntityChoiceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WeightedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(EntityChoiceAction::actions)
    ).apply(i, EntityChoiceAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        WeightedActionHolder holder = WeightedRandomSelector.selectRandomByWeight(this.actions);
        if (holder != null) holder.element.execute(source, target);
    }

    private record WeightedActionHolder(BiEntityAction element,
                                        int weight) implements WeightedRandomSelector.WeightGetter {
        public static final Codec<WeightedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                BiEntityAction.CODEC.fieldOf("element").forGetter(WeightedActionHolder::element),
                Codec.INT.optionalFieldOf("weight", 0).forGetter(WeightedActionHolder::weight)
        ).apply(i, WeightedActionHolder::new));
    }
}
