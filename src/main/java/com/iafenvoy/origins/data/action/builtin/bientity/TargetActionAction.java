package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record TargetActionAction(EntityAction action) implements BiEntityAction {
    public static final MapCodec<TargetActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(TargetActionAction::action)
    ).apply(i, TargetActionAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source, @NotNull Entity target) {
        this.action.accept(target);
    }
}
