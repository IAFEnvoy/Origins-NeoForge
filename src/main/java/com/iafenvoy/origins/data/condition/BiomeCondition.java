package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BiomeCondition {
    Codec<BiomeCondition> CODEC = DefaultedCodec.registryDispatch(ConditionRegistries.BIOME_CONDITION, BiomeCondition::codec, Function.identity(), () -> AlwaysTrueCondition.INSTANCE);

    static MapCodec<BiomeCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, AlwaysTrueCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BiomeCondition> codec();

    boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos);
}
