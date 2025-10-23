package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.Origins;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public final class ActionRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends BiEntityAction>>> BI_ENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "bi_entity_action"));
    public static final ResourceKey<Registry<MapCodec<? extends BlockAction>>> BLOCK_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "block_action"));
    public static final ResourceKey<Registry<MapCodec<? extends EntityAction>>> ENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "entity_action"));
    public static final ResourceKey<Registry<MapCodec<? extends ItemAction>>> ITEM_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "item_action"));

    public static final Registry<MapCodec<? extends BiEntityAction>> BI_ENTITY_ACTION = new MappedRegistry<>(BI_ENTITY_ACTION_KEY, Lifecycle.stable());
    public static final Registry<MapCodec<? extends BlockAction>> BLOCK_ACTION = new MappedRegistry<>(BLOCK_ACTION_KEY, Lifecycle.stable());
    public static final Registry<MapCodec<? extends EntityAction>> ENTITY_ACTION = new MappedRegistry<>(ENTITY_ACTION_KEY, Lifecycle.stable());
    public static final Registry<MapCodec<? extends ItemAction>> ITEM_ACTION = new MappedRegistry<>(ITEM_ACTION_KEY, Lifecycle.stable());

    @SubscribeEvent
    public static void newRegistries(NewRegistryEvent event) {
        event.register(BI_ENTITY_ACTION);
        event.register(BLOCK_ACTION);
        event.register(ENTITY_ACTION);
        event.register(ITEM_ACTION);
    }
}
