package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record BlockActionAction(BlockAction action) implements EntityAction {
    public static final MapCodec<BlockActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockAction.CODEC.fieldOf("event").forGetter(BlockActionAction::action)
    ).apply(i, BlockActionAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        this.action.execute(source.level(), source.blockPosition(), Direction.DOWN);
    }
}
