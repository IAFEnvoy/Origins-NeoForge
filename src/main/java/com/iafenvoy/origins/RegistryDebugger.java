package com.iafenvoy.origins;

import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.badge.BadgeRegistries;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.data.power.PowerRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.List;

//TODO::Configs to control prints
@EventBusSubscriber
public final class RegistryDebugger {
    private static final List<Registry<?>> BUILTIN_REGISTRIES = List.of(
            ActionRegistries.BI_ENTITY_ACTION, ActionRegistries.BLOCK_ACTION, ActionRegistries.ENTITY_ACTION, ActionRegistries.ITEM_ACTION,
            BadgeRegistries.BADGE_TYPE,
            ConditionRegistries.BI_ENTITY_CONDITION, ConditionRegistries.BIOME_CONDITION, ConditionRegistries.BLOCK_CONDITION, ConditionRegistries.DAMAGE_CONDITION,
            ConditionRegistries.ENTITY_CONDITION, ConditionRegistries.FLUID_CONDITION, ConditionRegistries.ITEM_CONDITION,
            PowerRegistries.POWER_TYPE
    );
    private static final List<ResourceKey<? extends Registry<?>>> DYNAMIC_REGISTRIES = List.of(
            BadgeRegistries.BADGE_KEY,
            LayerRegistries.LAYER_KEY,
            OriginRegistries.ORIGIN_KEY,
            PowerRegistries.POWER_KEY
    );

    @SubscribeEvent
    public static void afterBuiltinLoaded(FMLLoadCompleteEvent event) {
        Origins.LOGGER.debug("Origins builtin registries loaded, print object counts.");
        for (Registry<?> registry : BUILTIN_REGISTRIES)
            Origins.LOGGER.debug("Registry: {}, objects count: {}", registry.key().location(), registry.stream().count());
    }

    @SubscribeEvent
    public static void afterDatapackReloaded(TagsUpdatedEvent event) {
        Origins.LOGGER.info("Origins dynamic registries loaded, print object counts.");
        for (ResourceKey<? extends Registry<?>> key : DYNAMIC_REGISTRIES)
            Origins.LOGGER.info("Registry: {}, objects count: {}", key.location(), event.getRegistryAccess().registryOrThrow(key).stream().count());
    }
}
