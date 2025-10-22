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
    public static final ResourceKey<Registry<ConditionType>> CONDITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "condition"));
    public static final Registry<ConditionType> CONDITION = new MappedRegistry<>(CONDITION_KEY, Lifecycle.stable());

    @SubscribeEvent
    public static void newRegistries(NewRegistryEvent event) {
        event.register(CONDITION);
    }
}
