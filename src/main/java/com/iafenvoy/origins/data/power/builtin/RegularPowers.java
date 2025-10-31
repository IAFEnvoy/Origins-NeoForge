package com.iafenvoy.origins.data.power.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.EmptyPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.builtin.regular.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class RegularPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EmptyPower>> EMPTY = REGISTRY.register("empty", () -> EmptyPower.CODEC);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<BurnPower>> BURN = REGISTRY.register("burn", () -> BurnPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<CreativeFlightPower>> CREATIVE_FLIGHT = REGISTRY.register("creative_flight", () -> CreativeFlightPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<DamageOverTimePower>> DAMAGE_OVER_TIME = REGISTRY.register("damage_over_time", () -> DamageOverTimePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<DisableRegenPower>> DISABLE_REGEN = REGISTRY.register("disable_regen", () -> DisableRegenPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EffectImmunityPower>> EFFECT_IMMUNITY = REGISTRY.register("effect_immunity", () -> EffectImmunityPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ElytraFlightPower>> ELYTRA_FLIGHT = REGISTRY.register("elytra_flight", () -> ElytraFlightPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EntityGlowPower>> ENTITY_GLOW = REGISTRY.register("entity_glow", () -> EntityGlowPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EntitySetPower>> ENTITY_SET = REGISTRY.register("entity_set", () -> EntitySetPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ExhaustPower>> EXHAUST = REGISTRY.register("exhaust", () -> ExhaustPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<FireImmunityPower>> FIRE_IMMUNITY = REGISTRY.register("fire_immunity", () -> FireImmunityPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<FreezePower>> FREEZE = REGISTRY.register("freeze", () -> FreezePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<IgnoreWaterPower>> IGNORE_WATER = REGISTRY.register("ignore_water", () -> IgnoreWaterPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<InvulnerabilityPower>> INVULNERABILITY = REGISTRY.register("invulnerability", () -> InvulnerabilityPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<NightVisionPower>> NIGHT_VISION = REGISTRY.register("night_vision", () -> NightVisionPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<SelfGlowPower>> SELF_GLOW = REGISTRY.register("self_glow", () -> SelfGlowPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<TooltipPower>> TOOLTIP = REGISTRY.register("tooltip", () -> TooltipPower.CODEC);
}
