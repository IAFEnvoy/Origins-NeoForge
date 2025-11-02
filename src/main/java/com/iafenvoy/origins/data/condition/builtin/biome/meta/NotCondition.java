package com.iafenvoy.origins.data.condition.builtin.biome.meta;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record NotCondition(BiomeCondition condition) implements BiomeCondition {
    public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiomeCondition.CODEC.fieldOf("condition").forGetter(NotCondition::new)
    ).apply(i, NotCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos) {
        return !this.condition.test(biome, pos);
    }
}
