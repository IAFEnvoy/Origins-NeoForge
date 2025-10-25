package com.iafenvoy.origins.data.power.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.builtin.regular.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class RegularPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<BurnPower>> BURN = REGISTRY.register("burn", () -> BurnPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<CreativeFlightPower>> CREATIVE_FLIGHT = REGISTRY.register("creative_flight", () -> CreativeFlightPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<DamageOverTimePower>> DAMAGE_OVER_TIME = REGISTRY.register("damage_over_time", () -> DamageOverTimePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<DisableRegenPower>> DISABLE_REGEN = REGISTRY.register("disable_regen", () -> DisableRegenPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EffectImmunityPower>> EFFECT_IMMUNITY = REGISTRY.register("effect_immunity", () -> EffectImmunityPower.CODEC);
}
