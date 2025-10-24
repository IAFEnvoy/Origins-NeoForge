package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record HolderActionAction(EntityAction action) implements ItemAction {
    public static final MapCodec<HolderActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(HolderActionAction::action)
    ).apply(i, HolderActionAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        this.action.accept(source);
    }
}
