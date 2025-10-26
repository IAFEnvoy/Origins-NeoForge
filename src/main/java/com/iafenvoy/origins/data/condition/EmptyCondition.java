package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public enum EmptyCondition implements BiEntityCondition, BiomeCondition, BlockCondition, DamageCondition, EntityCondition, FluidCondition, ItemCondition {
    INSTANCE;
    public static final MapCodec<EmptyCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<EmptyCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        return true;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return true;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return true;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome) {
        return true;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return true;
    }
}
