package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record IfElseListAction(List<ConditionedActionHolder> actions) implements ItemAction {
    public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ConditionedActionHolder.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions)
    ).apply(i, IfElseListAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        for (ConditionedActionHolder holder : this.actions)
            if (holder.condition.test(level, stack)) {
                holder.action.execute(level, source, stack);
                break;
            }
    }

    private record ConditionedActionHolder(ItemCondition condition, ItemAction action) {
        public static final Codec<ConditionedActionHolder> CODEC = RecordCodecBuilder.create(i -> i.group(
                ItemCondition.CODEC.fieldOf("condition").forGetter(ConditionedActionHolder::condition),
                ItemAction.CODEC.fieldOf("action").forGetter(ConditionedActionHolder::action)
        ).apply(i, ConditionedActionHolder::new));
    }
}
