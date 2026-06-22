package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.loot.condition.OriginLootCondition;
import com.iafenvoy.origins.loot.condition.PowerLootCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsLootItemConditions {
    // 26.1：战利品条件类型注册表现在直接持有 MapCodec。
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> REGISTRY = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<OriginLootCondition>> ORIGIN = REGISTRY.register("origin", () -> OriginLootCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<PowerLootCondition>> POWER = REGISTRY.register("power", () -> PowerLootCondition.CODEC);
}
