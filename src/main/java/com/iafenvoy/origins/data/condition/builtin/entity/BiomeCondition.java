package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BiomeCondition(List<Holder<Biome>> biome, com.iafenvoy.origins.data.condition.BiomeCondition condition) implements EntityCondition {
    public static final MapCodec<BiomeCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CombinedCodecs.BIOME.optionalFieldOf("biome", List.of()).forGetter(BiomeCondition::biome),
            com.iafenvoy.origins.data.condition.BiomeCondition.optionalCodec("condition").forGetter(BiomeCondition::condition)
    ).apply(i, BiomeCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
        return (this.biome.isEmpty() || this.biome.contains(biome)) & this.condition.test(biome, entity.blockPosition());
    }
}
