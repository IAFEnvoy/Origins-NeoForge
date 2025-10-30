package com.iafenvoy.origins.data.condition.builtin.biome.meta;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BiomeAndCondition(List<BiomeCondition> conditions) implements BiomeCondition {
    public static final MapCodec<BiomeAndCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiomeCondition.CODEC.listOf().fieldOf("conditions").forGetter(BiomeAndCondition::conditions)
    ).apply(i, BiomeAndCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome) {
        return this.conditions.stream().allMatch(x -> x.test(biome));
    }
}
