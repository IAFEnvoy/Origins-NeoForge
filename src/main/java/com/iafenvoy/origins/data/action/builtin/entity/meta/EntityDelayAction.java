package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.Timeout;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntityDelayAction(EntityAction action, int ticks) implements EntityAction {
    public static final MapCodec<EntityDelayAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(EntityDelayAction::action),
            Codec.INT.fieldOf("ticks").forGetter(EntityDelayAction::ticks)
    ).apply(i, EntityDelayAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        Timeout.create(this.ticks, () -> this.action.accept(source));
    }
}
