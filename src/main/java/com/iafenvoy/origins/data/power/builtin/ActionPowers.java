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
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnBlockPlacePower>> ACTION_ON_BLOCK_PLACE = REGISTRY.register("action_on_block_place", () -> ActionOnBlockPlacePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnBlockUsePower>> ACTION_ON_BLOCK_USE = REGISTRY.register("action_on_block_use", () -> ActionOnBlockUsePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnCallbackPower>> ACTION_ON_CALLBACK = REGISTRY.register("action_on_callback", () -> ActionOnCallbackPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnDeathPower>> ACTION_ON_DEATH = REGISTRY.register("action_on_death", () -> ActionOnDeathPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnEntityUsePower>> ACTION_ON_ENTITY_USE = REGISTRY.register("action_on_entity_use", () -> ActionOnEntityUsePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnHitPower>> ACTION_ON_HIT = REGISTRY.register("action_on_hit", () -> ActionOnHitPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnItemPickupPower>> ACTION_ON_ITEM_PICKUP = REGISTRY.register("action_on_item_pickup", () -> ActionOnItemPickupPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnItemUsePower>> ACTION_ON_ITEM_USE = REGISTRY.register("action_on_item_use", () -> ActionOnItemUsePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnLandPower>> ACTION_ON_LAND = REGISTRY.register("action_on_land", () -> ActionOnLandPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOnWakeUpPower>> ACTION_ON_WAKE_UP = REGISTRY.register("action_on_wake_up", () -> ActionOnWakeUpPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionOverTimePower>> ACTION_OVER_TIME = REGISTRY.register("action_over_time", () -> ActionOverTimePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionWhenDamageTakenPower>> ACTION_WHEN_DAMAGE_TAKEN = REGISTRY.register("action_when_damage_taken", () -> ActionWhenDamageTakenPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActionWhenHitPower>> ACTION_WHEN_HIT = REGISTRY.register("action_when_hit", () -> ActionWhenHitPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ActiveSelfPower>> ACTIVE_SELF = REGISTRY.register("active_self", () -> ActiveSelfPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<AttackerActionWhenHitPower>> ATTACKER_ACTION_WHEN_HIT = REGISTRY.register("attacker_action_when_hit", () -> AttackerActionWhenHitPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<SelfActionOnHitPower>> SELF_ACTION_ON_HIT = REGISTRY.register("self_action_on_hit", () -> SelfActionOnHitPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<SelfActionOnKillPower>> SELF_ACTION_ON_KILL = REGISTRY.register("self_action_on_kill", () -> SelfActionOnKillPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<SelfActionWhenHitPower>> SELF_ACTION_WHEN_HIT = REGISTRY.register("self_action_when_hit", () -> SelfActionWhenHitPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<TargetActionOnHitPower>> TARGET_ACTION_ON_HIT = REGISTRY.register("target_action_on_hit", () -> TargetActionOnHitPower.CODEC);
}
