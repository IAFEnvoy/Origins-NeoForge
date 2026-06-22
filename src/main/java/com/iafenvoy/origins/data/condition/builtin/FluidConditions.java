package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.fluid.meta.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.iafenvoy.origins.data.condition.SimpleConditions.createFluid;

public final class FluidConditions {
    public static final DeferredRegister<MapCodec<? extends FluidCondition>> REGISTRY = DeferredRegister
            .create(ConditionRegistries.FLUID_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY
            .register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    // 列表
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<? extends FluidCondition>> EMPTY = REGISTRY
            .register("empty", () -> createFluid(FluidState::isEmpty));
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY
            .register("in_tag", () -> InTagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<? extends FluidCondition>> STILL = REGISTRY
            .register("still", () -> createFluid(FluidState::isSource));
    // 元条件
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<AndCondition>> AND = REGISTRY
            .register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY
            .register("chance", () -> ChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY
            .register("constant", () -> ConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<NotCondition>> NOT = REGISTRY
            .register("not", () -> NotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<OrCondition>> OR = REGISTRY
            .register("or", () -> OrCondition.CODEC);
}
