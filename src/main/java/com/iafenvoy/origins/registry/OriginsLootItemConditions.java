package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.loot.condition.OriginLootCondition;
import com.iafenvoy.origins.loot.condition.PowerLootCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsLootItemConditions {
    public static final DeferredRegister<LootItemConditionType> REGISTRY = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> ORIGIN = REGISTRY.register("origin", () -> new LootItemConditionType(OriginLootCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> POWER = REGISTRY.register("power", () -> new LootItemConditionType(PowerLootCondition.CODEC));
}
