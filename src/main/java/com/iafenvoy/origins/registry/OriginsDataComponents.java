package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class OriginsDataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Holder<Layer>>>> ORB_LAYERS = REGISTRY.register("orb_layers", () -> DataComponentType.<List<Holder<Layer>>>builder().persistent(Layer.CODEC.listOf()).build());
}
