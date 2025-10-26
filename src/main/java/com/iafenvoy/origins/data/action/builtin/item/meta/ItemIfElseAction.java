package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ItemIfElseAction(ItemCondition condition, ItemAction ifAction,
                               ItemAction elseAction) implements ItemAction {
    public static final MapCodec<ItemIfElseAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.CODEC.fieldOf("condition").forGetter(ItemIfElseAction::condition),
            ItemAction.CODEC.fieldOf("if_action").forGetter(ItemIfElseAction::ifAction),
            ItemAction.optionalCodec("else_action").forGetter(ItemIfElseAction::elseAction)
    ).apply(i, ItemIfElseAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        if (this.condition.test(level, stack)) this.ifAction.execute(level, source, stack);
        else this.elseAction.execute(level, source, stack);
    }
}
