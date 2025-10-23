package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface BiomeCondition extends Predicate<Holder<Biome>> {
    Codec<BiomeCondition> CODEC = ConditionRegistries.BIOME_CONDITION.byNameCodec().dispatch("type", BiomeCondition::codec, x -> x);

    @NotNull
    MapCodec<? extends BiomeCondition> codec();

    @Override
    boolean test(@NotNull Holder<Biome> biome);
}
