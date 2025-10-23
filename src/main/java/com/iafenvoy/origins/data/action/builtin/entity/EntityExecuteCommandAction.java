package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.CommandHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntityExecuteCommandAction(String command) implements EntityAction {
    public static final MapCodec<EntityExecuteCommandAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(EntityExecuteCommandAction::command)
    ).apply(i, EntityExecuteCommandAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (source.level() instanceof ServerLevel serverLevel)
            CommandHelper.executeCommand(serverLevel.getServer(), this.command);
    }
}
