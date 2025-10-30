package com.iafenvoy.origins.data.condition.builtin.biome.meta;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record BiomeConstantCondition(boolean value) implements BiomeCondition {
    public static final MapCodec<BiomeConstantCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("value").forGetter(BiomeConstantCondition::value)
    ).apply(i, BiomeConstantCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome) {
        return this.value;
    }
}
