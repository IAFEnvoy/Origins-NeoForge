package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.EmptyCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.BiomeInTagCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BiomeConditions {
    public static final DeferredRegister<MapCodec<? extends BiomeCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BIOME_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<EmptyCondition>> EMPTY = REGISTRY.register("empty", () -> EmptyCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeInTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> BiomeInTagCondition.CODEC);
}
