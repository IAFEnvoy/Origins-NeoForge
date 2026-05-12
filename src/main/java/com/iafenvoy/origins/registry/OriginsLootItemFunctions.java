package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.loot.function.AddPowerLootFunction;
import com.iafenvoy.origins.loot.function.RemovePowerLootFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsLootItemFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> REGISTRY = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<AddPowerLootFunction>> ADD_POWER = REGISTRY.register("add_power", () -> new LootItemFunctionType<>(AddPowerLootFunction.MAP_CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RemovePowerLootFunction>> REMOVE_POWER = REGISTRY.register("remove_power", () -> new LootItemFunctionType<>(RemovePowerLootFunction.MAP_CODEC));
}
