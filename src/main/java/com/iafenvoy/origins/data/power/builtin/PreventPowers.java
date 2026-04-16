package com.iafenvoy.origins.data.power.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.builtin.prevent.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class PreventPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<PreventBlockSelectionPower>> PREVENT_BLOCK_SELECTION = REGISTRY.register("prevent_block_selection", () -> PreventBlockSelectionPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<PreventEntityRenderPower>> PREVENT_ENTITY_RENDER = REGISTRY.register("prevent_entity_render", () -> PreventEntityRenderPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<PreventGameEventPower>> PREVENT_GAME_EVENT = REGISTRY.register("prevent_game_event", () -> PreventGameEventPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<PreventItemUsePower>> PREVENT_ITEM_USE = REGISTRY.register("prevent_item_use", () -> PreventItemUsePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<PreventSleepPower>> PREVENT_SLEEP = REGISTRY.register("prevent_sleep", () -> PreventSleepPower.CODEC);
}
