package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.Timeout;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ItemDelayAction(ItemAction action, int ticks) implements ItemAction {
    public static final MapCodec<ItemDelayAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemAction.CODEC.fieldOf("action").forGetter(ItemDelayAction::action),
            Codec.INT.fieldOf("ticks").forGetter(ItemDelayAction::ticks)
    ).apply(i, ItemDelayAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        Timeout.create(this.ticks, () -> this.action.accept(level, source, stack));
    }
}
