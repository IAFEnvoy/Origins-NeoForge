package com.iafenvoy.origins.data.condition.builtin.biome.meta;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record OrCondition(List<BiomeCondition> conditions) implements BiomeCondition {
    public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiomeCondition.CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::conditions)
    ).apply(i, OrCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos) {
        return this.conditions.stream().anyMatch(x -> x.test(biome, pos));
    }
}
