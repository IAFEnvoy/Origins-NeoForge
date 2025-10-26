package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public interface BiomeCondition {
    Codec<BiomeCondition> CODEC = ConditionRegistries.BIOME_CONDITION.byNameCodec().dispatch("type", BiomeCondition::codec, x -> x);

    static MapCodec<BiomeCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BiomeCondition> codec();

    boolean test(@NotNull Holder<Biome> biome);
}
