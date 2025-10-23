package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public enum MountAction implements BiEntityAction {
    INSTANCE;
    public static final MapCodec<MountAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source, @NotNull Entity target) {
        source.startRiding(target);
    }
}
