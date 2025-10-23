package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.Origins;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public final class ConditionRegistries {
    public static final ResourceKey<Registry<ConditionType<BiEntityCondition>>> BI_ENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "bi_entity_condition"));
    public static final ResourceKey<Registry<ConditionType<BiomeCondition>>> BIOME_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "biome_condition"));
    public static final ResourceKey<Registry<ConditionType<BlockCondition>>> BLOCK_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "block_condition"));
    public static final ResourceKey<Registry<ConditionType<DamageCondition>>> DAMAGE_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "damage_condition"));
    public static final ResourceKey<Registry<ConditionType<EntityCondition>>> ENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "entity_condition"));
    public static final ResourceKey<Registry<ConditionType<FluidCondition>>> FLUID_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "fluid_condition"));
    public static final ResourceKey<Registry<ConditionType<ItemCondition>>> ITEM_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "item_condition"));

    public static final Registry<ConditionType<BiEntityCondition>> BI_ENTITY_CONDITION = new MappedRegistry<>(BI_ENTITY_CONDITION_KEY, Lifecycle.stable());
    public static final Registry<ConditionType<BiomeCondition>> BIOME_CONDITION = new MappedRegistry<>(BIOME_CONDITION_KEY, Lifecycle.stable());
    public static final Registry<ConditionType<BlockCondition>> BLOCK_CONDITION = new MappedRegistry<>(BLOCK_CONDITION_KEY, Lifecycle.stable());
    public static final Registry<ConditionType<DamageCondition>> DAMAGE_CONDITION = new MappedRegistry<>(DAMAGE_CONDITION_KEY, Lifecycle.stable());
    public static final Registry<ConditionType<EntityCondition>> ENTITY_CONDITION = new MappedRegistry<>(ENTITY_CONDITION_KEY, Lifecycle.stable());
    public static final Registry<ConditionType<FluidCondition>> FLUID_CONDITION = new MappedRegistry<>(FLUID_CONDITION_KEY, Lifecycle.stable());
    public static final Registry<ConditionType<ItemCondition>> ITEM_CONDITION = new MappedRegistry<>(ITEM_CONDITION_KEY, Lifecycle.stable());

    @SubscribeEvent
    public static void newRegistries(NewRegistryEvent event) {
        event.register(BI_ENTITY_CONDITION);
        event.register(BIOME_CONDITION);
        event.register(BLOCK_CONDITION);
        event.register(DAMAGE_CONDITION);
        event.register(ENTITY_CONDITION);
        event.register(FLUID_CONDITION);
        event.register(ITEM_CONDITION);
    }
}
