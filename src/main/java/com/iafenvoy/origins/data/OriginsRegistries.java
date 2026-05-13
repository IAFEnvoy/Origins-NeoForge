package com.iafenvoy.origins.data;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.badge.BadgeRegistries;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.component.PowerComponentRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;

@EventBusSubscriber
public final class OriginsRegistries {
    private static final List<Registry<?>> BUILTIN_REGISTRIES = List.of(
            ActionRegistries.BI_ENTITY_ACTION, ActionRegistries.BLOCK_ACTION, ActionRegistries.ENTITY_ACTION, ActionRegistries.ITEM_ACTION,
            BadgeRegistries.BADGE_TYPE,
            ConditionRegistries.BI_ENTITY_CONDITION, ConditionRegistries.BIOME_CONDITION, ConditionRegistries.BLOCK_CONDITION, ConditionRegistries.DAMAGE_CONDITION,
            ConditionRegistries.ENTITY_CONDITION, ConditionRegistries.FLUID_CONDITION, ConditionRegistries.ITEM_CONDITION,
            PowerRegistries.POWER_TYPE, PowerComponentRegistries.POWER_COMPONENT_TYPE
    );
    private static final List<ResourceKey<? extends Registry<?>>> DYNAMIC_REGISTRIES = List.of(BadgeRegistries.BADGE_KEY, LayerRegistries.LAYER_KEY, OriginRegistries.ORIGIN_KEY, PowerRegistries.POWER_KEY);

    @SubscribeEvent
    public static void newDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        // I need to control order of these registries, so I put them here.
        // Builtin ones still in their own classes.
        event.dataPackRegistry(BadgeRegistries.BADGE_KEY, Badge.DIRECT_CODEC, Badge.DIRECT_CODEC);
        event.dataPackRegistry(PowerRegistries.POWER_KEY, Power.DIRECT_CODEC, Power.DIRECT_CODEC);
        event.dataPackRegistry(OriginRegistries.ORIGIN_KEY, Origin.DIRECT_CODEC, Origin.DIRECT_CODEC);
        event.dataPackRegistry(LayerRegistries.LAYER_KEY, Layer.DIRECT_CODEC, Layer.DIRECT_CODEC);
    }

    @SubscribeEvent
    public static void afterBuiltinLoaded(FMLLoadCompleteEvent event) {
        if (!FMLEnvironment.production || OriginsConfig.INSTANCE.debug.builtinRegistries.getValue()) {
            Origins.LOGGER.info("Origins builtin registries loaded, print object counts.");
            for (Registry<?> registry : BUILTIN_REGISTRIES)
                Origins.LOGGER.info("Builtin Registry: {}, objects count: {}", registry.key().location(), registry.stream().count());
        }
    }

    @SubscribeEvent
    public static void afterDynamicLoaded(TagsUpdatedEvent event) {
        if (!FMLEnvironment.production || OriginsConfig.INSTANCE.debug.dynamicRegistries.getValue()) {
            RegistryAccess access = event.getRegistryAccess();
            Origins.LOGGER.info("Origins dynamic registries loaded, print object counts.");
            for (ResourceKey<? extends Registry<?>> key : DYNAMIC_REGISTRIES)
                Origins.LOGGER.info("Dynamic Registry: {}, objects count: {}", key.location(), access.registryOrThrow(key).stream().count());
        }
    }
}
