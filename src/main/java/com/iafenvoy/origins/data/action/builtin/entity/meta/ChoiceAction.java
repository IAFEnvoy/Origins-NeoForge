package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChoiceAction(List<WeightedActionHolder> actions) implements EntityAction {
    public static final MapCodec<ChoiceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WeightedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(ChoiceAction::actions)
    ).apply(i, ChoiceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        WeightedActionHolder holder = WeightedRandomSelector.selectRandomByWeight(this.actions);
        if (holder != null) holder.element.execute(source);
    }

    private record WeightedActionHolder(EntityAction element,
                                        int weight) implements WeightedRandomSelector.WeightGetter {
        public static final Codec<WeightedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                EntityAction.CODEC.fieldOf("element").forGetter(WeightedActionHolder::element),
                Codec.INT.optionalFieldOf("weight", 0).forGetter(WeightedActionHolder::weight)
        ).apply(i, WeightedActionHolder::new));
    }
}
