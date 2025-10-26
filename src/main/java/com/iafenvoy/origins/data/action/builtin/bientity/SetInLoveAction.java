package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public enum SetInLoveAction implements BiEntityAction {
    INSTANCE;
    public static final MapCodec<SetInLoveAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        if (target instanceof Animal animal)
            animal.setInLove(source instanceof Player player ? player : null);
    }
}
