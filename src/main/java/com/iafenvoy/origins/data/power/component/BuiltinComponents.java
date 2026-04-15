package com.iafenvoy.origins.data.power.component;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.iafenvoy.origins.data.power.component.builtin.EmptyComponent;
import com.iafenvoy.origins.data.power.component.builtin.EntitySetComponent;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BuiltinComponents {
    public static final DeferredRegister<MapCodec<? extends PowerComponent>> REGISTRY = DeferredRegister.create(PowerComponentRegistries.POWER_COMPONENT_TYPE, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<EmptyComponent>> EMPTY = REGISTRY.register(Constants.EMPTY_KEY, () -> EmptyComponent.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<CooldownComponent>> COOLDOWN = REGISTRY.register("cooldown", () -> CooldownComponent.CODEC);
    public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<EntitySetComponent>> ENTITY_SET = REGISTRY.register("entity_set", () -> EntitySetComponent.CODEC);
    public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<ResourceComponent>> RESOURCE = REGISTRY.register("resource", () -> ResourceComponent.CODEC);
}
