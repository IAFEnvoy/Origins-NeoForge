package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.builtin.biome.BiomeInTagCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BiomeConditions {
    public static final DeferredRegister<MapCodec<? extends BiomeCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BIOME_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeInTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> BiomeInTagCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeAndCondition>> AND = REGISTRY.register("and", () -> BiomeAndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeChanceCondition>> CHANCE = REGISTRY.register("chance", () -> BiomeChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> BiomeConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeNotCondition>> NOT = REGISTRY.register("not", () -> BiomeNotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<BiomeOrCondition>> OR = REGISTRY.register("or", () -> BiomeOrCondition.CODEC);
}
