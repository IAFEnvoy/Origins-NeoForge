package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.builtin.biome.BiomeInTagCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BiomeConditions {
    public static final DeferredRegister<MapCodec<? extends BiomeCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BIOME_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeInTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> BiomeInTagCondition.CODEC);
}
