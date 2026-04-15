package com.iafenvoy.origins.data.condition.builtin.biome;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record TemperatureCondition(Comparison comparison) implements BiomeCondition {
    public static final MapCodec<TemperatureCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.forGetter(TemperatureCondition::comparison)
    ).apply(i, TemperatureCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos) {
        return this.comparison.compare(biome.value().getBaseTemperature());
    }
}
