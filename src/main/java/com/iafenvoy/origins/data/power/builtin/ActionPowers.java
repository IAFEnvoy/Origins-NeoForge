package com.iafenvoy.origins.data.power.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.builtin.action.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ActionPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnBeingUsedPower>> ACTION_ON_BEING_USED = REGISTRY.register("action_on_being_used", () -> ActionOnBeingUsedPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnBlockBreakPower>> ACTION_ON_BLOCK_BREAK = REGISTRY.register("action_on_block_break", () -> ActionOnBlockBreakPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnItemUsePower>> ACTION_ON_ITEM_USE = REGISTRY.register("action_on_item_use", () -> ActionOnItemUsePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnWakeUpPower>> ACTION_ON_WAKE_UP = REGISTRY.register("action_on_wake_up", () -> ActionOnWakeUpPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOverTimePower>> ACTION_OVER_TIME = REGISTRY.register("action_over_time", () -> ActionOverTimePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionWhenHitPower>> ACTION_WHEN_HIT = REGISTRY.register("action_when_hit", () -> ActionWhenHitPower.CODEC);
}
