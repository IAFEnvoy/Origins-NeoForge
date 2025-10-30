package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.FluidEmptyCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.FluidInTagCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.StillCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class FluidConditions {
    public static final DeferredRegister<MapCodec<? extends FluidCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.FLUID_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<FluidEmptyCondition>> EMPTY = REGISTRY.register("empty", () -> FluidEmptyCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<FluidInTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> FluidInTagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<StillCondition>> STILL = REGISTRY.register("still", () -> StillCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<FluidAndCondition>> AND = REGISTRY.register("and", () -> FluidAndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<FluidChanceCondition>> CHANCE = REGISTRY.register("chance", () -> FluidChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<FluidConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> FluidConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<FluidNotCondition>> NOT = REGISTRY.register("not", () -> FluidNotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<FluidOrCondition>> OR = REGISTRY.register("or", () -> FluidOrCondition.CODEC);
}
