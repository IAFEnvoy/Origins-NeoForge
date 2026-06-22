package com.iafenvoy.origins.data.condition.builtin.biome;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record InTagCondition(TagKey<Biome> tag, boolean inverted) implements BiomeCondition {
    public static final MapCodec<InTagCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            TagKey.codec(Registries.BIOME).fieldOf("tag").forGetter(InTagCondition::tag),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(InTagCondition::inverted)
    ).apply(i, InTagCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiomeCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos) {
        return biome.is(this.tag) ^ this.inverted;
    }
}
