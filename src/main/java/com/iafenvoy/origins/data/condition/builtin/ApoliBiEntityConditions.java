package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.builtin.bientity.meta.EqualCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ApoliBiEntityConditions {
    public static final DeferredRegister<MapCodec<? extends BiEntityCondition>> REGISTRY =
            DeferredRegister.create(ConditionRegistries.BI_ENTITY_CONDITION, "apoli");

    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<EqualCondition>> EQUAL =
            REGISTRY.register("equal", () -> EqualCondition.CODEC);
}
