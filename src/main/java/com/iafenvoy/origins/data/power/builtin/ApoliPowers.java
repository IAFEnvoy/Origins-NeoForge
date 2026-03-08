package com.iafenvoy.origins.data.power.builtin;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFallingPower;
import com.iafenvoy.origins.data.power.builtin.regular.MultiplePower;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ApoliPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, "apoli");

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<MultiplePower>> MULTIPLE = REGISTRY.register("multiple", () -> MultiplePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyFallingPower>> MODIFY_FALLING = REGISTRY.register("modify_falling", () -> ModifyFallingPower.CODEC);
}
