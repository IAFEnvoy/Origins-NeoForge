package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ItemAndAction(List<ItemAction> actions) implements ItemAction {
    public static final MapCodec<ItemAndAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemAction.CODEC.listOf().fieldOf("actions").forGetter(ItemAndAction::actions)
    ).apply(i, ItemAndAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        this.actions.forEach(x -> x.accept(level, source, stack));
    }
}
