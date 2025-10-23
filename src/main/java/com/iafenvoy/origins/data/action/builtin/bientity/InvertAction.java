package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record InvertAction(BiEntityAction action) implements BiEntityAction {
    public static final MapCodec<InvertAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("action").forGetter(InvertAction::action)
    ).apply(i, InvertAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source, @NotNull Entity target) {
        this.action.accept(target, source);
    }
}
