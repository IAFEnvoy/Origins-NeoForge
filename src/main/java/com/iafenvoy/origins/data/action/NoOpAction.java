package com.iafenvoy.origins.data.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public enum NoOpAction implements BiEntityAction, BlockAction, EntityAction, ItemAction {
    INSTANCE;
    public static final MapCodec<NoOpAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<NoOpAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
    }

    @Override
    public void execute(@NotNull Entity source) {
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
    }
}
