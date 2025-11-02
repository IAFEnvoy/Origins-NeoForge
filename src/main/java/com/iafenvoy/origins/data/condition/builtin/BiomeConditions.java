package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.builtin.biome.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.HighHumidityCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.PrecipitationCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.TemperatureCondition;
import com.iafenvoy.origins.data.condition.builtin.biome.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BiomeConditions {
    public static final DeferredRegister<MapCodec<? extends BiomeCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BIOME_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<HighHumidityCondition>> HIGH_HUMIDITY = REGISTRY.register("high_humidity", () -> HighHumidityCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> InTagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<PrecipitationCondition>> PRECIPITATION = REGISTRY.register("precipitation", () -> PrecipitationCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<TemperatureCondition>> TEMPERATURE = REGISTRY.register("temperature", () -> TemperatureCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register("chance", () -> ChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> ConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
