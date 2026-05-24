package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data._common.CommandHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ExecuteCommandAction(String command) implements EntityAction, CommandHelper {
    public static final MapCodec<ExecuteCommandAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(ExecuteCommandAction::command)
    ).apply(i, ExecuteCommandAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        this.executeCommand(source, this.command);
    }
}
