package com.iafenvoy.origins.data.action;

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
public final class ActionRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends BiEntityAction>>> BI_ENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "bi_entity_action"));
    public static final ResourceKey<Registry<MapCodec<? extends BlockAction>>> BLOCK_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "block_action"));
    public static final ResourceKey<Registry<MapCodec<? extends EntityAction>>> ENTITY_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "entity_action"));
    public static final ResourceKey<Registry<MapCodec<? extends ItemAction>>> ITEM_ACTION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "item_action"));

    public static final DefaultedRegistry<MapCodec<? extends BiEntityAction>> BI_ENTITY_ACTION = new DefaultedMappedRegistry<>(Constants.NO_OP_KEY, BI_ENTITY_ACTION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends BlockAction>> BLOCK_ACTION = new DefaultedMappedRegistry<>(Constants.NO_OP_KEY, BLOCK_ACTION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends EntityAction>> ENTITY_ACTION = new DefaultedMappedRegistry<>(Constants.NO_OP_KEY, ENTITY_ACTION_KEY, Lifecycle.stable(), false);
    public static final DefaultedRegistry<MapCodec<? extends ItemAction>> ITEM_ACTION = new DefaultedMappedRegistry<>(Constants.NO_OP_KEY, ITEM_ACTION_KEY, Lifecycle.stable(), false);

    @SubscribeEvent
    public static void newRegistries(NewRegistryEvent event) {
        event.register(BI_ENTITY_ACTION);
        event.register(BLOCK_ACTION);
        event.register(ENTITY_ACTION);
        event.register(ITEM_ACTION);
    }
}
