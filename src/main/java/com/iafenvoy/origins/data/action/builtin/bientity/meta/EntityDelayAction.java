package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.util.Timeout;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntityDelayAction(BiEntityAction action, int ticks) implements BiEntityAction {
    public static final MapCodec<EntityDelayAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("action").forGetter(EntityDelayAction::action),
            Codec.INT.fieldOf("ticks").forGetter(EntityDelayAction::ticks)
    ).apply(i, EntityDelayAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source, @NotNull Entity target) {
        Timeout.create(this.ticks, () -> this.action.accept(source, target));
    }
}
