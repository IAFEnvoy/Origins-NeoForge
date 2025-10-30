package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.util.Timeout;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record BiEntityDelayAction(BiEntityAction action, int ticks) implements BiEntityAction {
    public static final MapCodec<BiEntityDelayAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("action").forGetter(BiEntityDelayAction::action),
            Codec.INT.fieldOf("ticks").forGetter(BiEntityDelayAction::ticks)
    ).apply(i, BiEntityDelayAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        Timeout.create(this.ticks, () -> this.action.execute(source, target));
    }
}
