package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public record EmitGameEventAction(Holder<GameEvent> event) implements EntityAction {
    public static final MapCodec<EmitGameEventAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            GameEvent.CODEC.fieldOf("action").forGetter(EmitGameEventAction::event)
    ).apply(i, EmitGameEventAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        source.gameEvent(this.event);
    }
}
