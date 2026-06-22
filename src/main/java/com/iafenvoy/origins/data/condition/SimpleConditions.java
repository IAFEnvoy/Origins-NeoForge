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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class SimpleConditions {
    public static MapCodec<? extends BiEntityCondition> createBiEntity(BiPredicate<Entity, Entity> predicate) {
        return new BiEntityCondition() {
            final MapCodec<? extends BiEntityCondition> codec = MapCodec.unit(this);

            @Override
            public @NotNull MapCodec<? extends BiEntityCondition> codec() {
                return this.codec;
            }

            @Override
            public boolean test(@NotNull Entity source, @NotNull Entity target) {
                return predicate.test(source, target);
            }
        }.codec();
    }

    public static MapCodec<? extends BiomeCondition> createBiome(BiPredicate<Holder<Biome>, BlockPos> predicate) {
        return new BiomeCondition() {
            final MapCodec<? extends BiomeCondition> codec = MapCodec.unit(this);

            @Override
            public @NotNull MapCodec<? extends BiomeCondition> codec() {
                return this.codec;
            }

            @Override
            public boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos) {
                return predicate.test(biome, pos);
            }
        }.codec();
    }

    public static MapCodec<? extends BlockCondition> createBlock(BiPredicate<Level, BlockPos> predicate) {
        return new BlockCondition() {
            final MapCodec<? extends BlockCondition> codec = MapCodec.unit(this);

            @Override
            public @NotNull MapCodec<? extends BlockCondition> codec() {
                return this.codec;
            }

            @Override
            public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
                return predicate.test(level, pos);
            }
        }.codec();
    }

    public static MapCodec<? extends DamageCondition> createDamage(BiPredicate<DamageSource, Float> predicate) {
        return new DamageCondition() {
            final MapCodec<? extends DamageCondition> codec = MapCodec.unit(this);

            @Override
            public @NotNull MapCodec<? extends DamageCondition> codec() {
                return this.codec;
            }

            @Override
            public boolean test(@NotNull DamageSource source, float amount) {
                return predicate.test(source, amount);
            }
        }.codec();
    }

    public static MapCodec<? extends EntityCondition> createEntity(Predicate<Entity> predicate) {
        return new EntityCondition() {
            final MapCodec<? extends EntityCondition> codec = MapCodec.unit(this);

            @Override
            public @NotNull MapCodec<? extends EntityCondition> codec() {
                return this.codec;
            }

            @Override
            public boolean test(@NotNull Entity source) {
                return predicate.test(source);
            }
        }.codec();
    }

    public static MapCodec<? extends FluidCondition> createFluid(Predicate<FluidState> predicate) {
        return new FluidCondition() {
            final MapCodec<? extends FluidCondition> codec = MapCodec.unit(this);

            @Override
            public @NotNull MapCodec<? extends FluidCondition> codec() {
                return this.codec;
            }

            @Override
            public boolean test(@NotNull FluidState state) {
                return predicate.test(state);
            }
        }.codec();
    }

    public static MapCodec<? extends ItemCondition> createItem(BiPredicate<Level, ItemStack> predicate) {
        return new ItemCondition() {
            final MapCodec<? extends ItemCondition> codec = MapCodec.unit(this);

            @Override
            public @NotNull MapCodec<? extends ItemCondition> codec() {
                return this.codec;
            }

            @Override
            public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
                return predicate.test(level, stack);
            }
        }.codec();
    }
}
