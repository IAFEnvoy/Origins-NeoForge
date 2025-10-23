package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

public interface BiomeCondition extends Predicate<Holder<Biome>> {
    Codec<BiomeCondition> CODEC = ConditionRegistries.BIOME_CONDITION.byNameCodec().dispatch("type", BiomeCondition::type, ConditionType::codec);

    ConditionType<BiomeCondition> type();

    @Override
    boolean test(Holder<Biome> biome);
}
