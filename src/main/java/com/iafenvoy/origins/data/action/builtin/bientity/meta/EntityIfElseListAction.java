package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EntityIfElseListAction(List<ConditionedActionHolder> actions) implements BiEntityAction {
    public static final MapCodec<EntityIfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(EntityIfElseListAction::actions)
    ).apply(i, EntityIfElseListAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        for (ConditionedActionHolder holder : this.actions)
            if (holder.condition.test(source, target)) {
                holder.action.execute(source, target);
                break;
            }
    }

    private record ConditionedActionHolder(BiEntityCondition condition, BiEntityAction action) {
        public static final Codec<ConditionedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                BiEntityCondition.CODEC.fieldOf("condition").forGetter(ConditionedActionHolder::condition),
                BiEntityAction.CODEC.fieldOf("action").forGetter(ConditionedActionHolder::action)
        ).apply(i, ConditionedActionHolder::new));
    }
}
