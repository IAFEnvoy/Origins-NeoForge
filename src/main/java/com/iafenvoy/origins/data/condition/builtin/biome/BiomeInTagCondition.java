package com.iafenvoy.origins.data.condition.builtin.biome;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record BiomeInTagCondition(TagKey<Biome> tag, boolean inverted) implements BiomeCondition {
    public static final MapCodec<BiomeInTagCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            TagKey.codec(Registries.BIOME).fieldOf("tag").forGetter(BiomeInTagCondition::tag),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(BiomeInTagCondition::inverted)
    ).apply(i, BiomeInTagCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome) {
        return biome.is(this.tag) ^ this.inverted;
    }
}
