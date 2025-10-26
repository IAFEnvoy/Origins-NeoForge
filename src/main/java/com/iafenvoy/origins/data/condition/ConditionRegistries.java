package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public final class ConditionRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends BiEntityCondition>>> BI_ENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "bi_entity_condition"));
    public static final ResourceKey<Registry<MapCodec<? extends BiomeCondition>>> BIOME_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "biome_condition"));
    public static final ResourceKey<Registry<MapCodec<? extends BlockCondition>>> BLOCK_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "block_condition"));
    public static final ResourceKey<Registry<MapCodec<? extends DamageCondition>>> DAMAGE_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "damage_condition"));
    public static final ResourceKey<Registry<MapCodec<? extends EntityCondition>>> ENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "entity_condition"));
    public static final ResourceKey<Registry<MapCodec<? extends FluidCondition>>> FLUID_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "fluid_condition"));
    public static final ResourceKey<Registry<MapCodec<? extends ItemCondition>>> ITEM_CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "item_condition"));

    public static final DefaultedRegistry<MapCodec<? extends BiEntityCondition>> BI_ENTITY_CONDITION = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, BI_ENTITY_CONDITION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends BiomeCondition>> BIOME_CONDITION = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, BIOME_CONDITION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends BlockCondition>> BLOCK_CONDITION = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, BLOCK_CONDITION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends DamageCondition>> DAMAGE_CONDITION = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, DAMAGE_CONDITION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends EntityCondition>> ENTITY_CONDITION = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, ENTITY_CONDITION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends FluidCondition>> FLUID_CONDITION = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, FLUID_CONDITION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends ItemCondition>> ITEM_CONDITION = new DefaultedMappedRegistry<>(Constants.EMPTY_KEY, ITEM_CONDITION_KEY, Lifecycle.stable(), false);

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
