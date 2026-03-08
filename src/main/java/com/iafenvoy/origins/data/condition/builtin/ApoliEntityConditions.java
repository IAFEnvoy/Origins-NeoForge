package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.FoodLevelCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.PowerActiveCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ApoliEntityConditions {
    public static final DeferredRegister<MapCodec<? extends EntityCondition>> REGISTRY =
            DeferredRegister.create(ConditionRegistries.ENTITY_CONDITION, "apoli");

    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<FoodLevelCondition>> FOOD_LEVEL =
            REGISTRY.register("food_level", () -> FoodLevelCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<PowerActiveCondition>> POWER_ACTIVE =
            REGISTRY.register("power_active", () -> PowerActiveCondition.CODEC);
}
