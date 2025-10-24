package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EntityIfElseListAction(List<ConditionedActionHolder> actions) implements EntityAction {
    public static final MapCodec<EntityIfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(EntityIfElseListAction::actions)
    ).apply(i, EntityIfElseListAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        for (ConditionedActionHolder holder : this.actions)
            if (holder.condition.test(source)) {
                holder.action.accept(source);
                break;
            }
    }

    private record ConditionedActionHolder(EntityCondition condition, EntityAction action) {
        public static final Codec<ConditionedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                EntityCondition.CODEC.fieldOf("condition").forGetter(ConditionedActionHolder::condition),
                EntityAction.CODEC.fieldOf("action").forGetter(ConditionedActionHolder::action)
        ).apply(i, ConditionedActionHolder::new));
    }
}
