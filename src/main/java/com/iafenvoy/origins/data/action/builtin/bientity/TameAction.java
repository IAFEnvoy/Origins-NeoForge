package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public enum TameAction implements BiEntityAction {
    INSTANCE;
    public static final MapCodec<TameAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source, @NotNull Entity target) {
        if (source instanceof Player player && target instanceof TamableAnimal ownable)
            ownable.tame(player);
    }
}
