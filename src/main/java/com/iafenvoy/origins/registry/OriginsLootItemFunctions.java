package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.loot.function.AddPowerLootFunction;
import com.iafenvoy.origins.loot.function.RemovePowerLootFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsLootItemFunctions {
    // 26.1：战利品函数类型注册表现在直接持有 MapCodec。
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> REGISTRY = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<AddPowerLootFunction>> ADD_POWER = REGISTRY.register("add_power", () -> AddPowerLootFunction.MAP_CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<RemovePowerLootFunction>> REMOVE_POWER = REGISTRY.register("remove_power", () -> RemovePowerLootFunction.MAP_CODEC);
}
