package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ItemIfElseAction(ItemCondition condition, ItemAction ifAction,
                               Optional<ItemAction> elseAction) implements ItemAction {
    public static final MapCodec<ItemIfElseAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.CODEC.fieldOf("condition").forGetter(ItemIfElseAction::condition),
            ItemAction.CODEC.fieldOf("if_action").forGetter(ItemIfElseAction::ifAction),
            ItemAction.CODEC.optionalFieldOf("else_action").forGetter(ItemIfElseAction::elseAction)
    ).apply(i, ItemIfElseAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        if (this.condition.test(level, stack)) this.ifAction.accept(level, source, stack);
        else this.elseAction.ifPresent(x -> x.accept(level, source, stack));
    }
}
