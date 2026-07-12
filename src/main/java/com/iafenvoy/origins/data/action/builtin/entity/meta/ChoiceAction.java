package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data._common.WeightedActionEntry;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChoiceAction(List<WeightedActionEntry<EntityAction>> actions) implements EntityAction {
    public static final MapCodec<ChoiceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WeightedActionEntry.codec(EntityAction.CODEC).listOf().fieldOf("actions").forGetter(ChoiceAction::actions)
    ).apply(i, ChoiceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        WeightedActionEntry<EntityAction> holder = WeightedRandomSelector.selectRandomByWeight(this.actions);
        if (holder != null) holder.element().execute(source);
    }
}
