package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.EmptyCondition;
import com.iafenvoy.origins.data.condition.builtin.block.*;
import com.iafenvoy.origins.data.condition.builtin.block.meta.BlockOffsetCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BlockConditions {
    public static final DeferredRegister<MapCodec<? extends BlockCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BLOCK_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<EmptyCondition>> EMPTY = REGISTRY.register("empty", () -> EmptyCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<AttachableCondition>> ATTACHABLE = REGISTRY.register("attachable", () -> AttachableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockEntityCondition>> BLOCK_ENTITY = REGISTRY.register("block_entity", () -> BlockEntityCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockIdCondition>> BLOCK = REGISTRY.register("block", () -> BlockIdCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<ExposedToSkyCondition>> EXPOSED_TO_SKY = REGISTRY.register("exposed_to_sky", () -> ExposedToSkyCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockFluidIdCondition>> FLUID = REGISTRY.register("fluid", () -> BlockFluidIdCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockInTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> BlockInTagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<LightBlockingCondition>> LIGHT_BLOCKING = REGISTRY.register("light_blocking", () -> LightBlockingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<MovementBlockingCondition>> MOVEMENT_BLOCKING = REGISTRY.register("movement_blocking", () -> MovementBlockingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockEntityNbtCondition>> NBT = REGISTRY.register("nbt", () -> BlockEntityNbtCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<ReplaceableCondition>> REPLACEABLE = REGISTRY.register("replaceable", () -> ReplaceableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<WaterLoggableCondition>> WATER_LOGGABLE = REGISTRY.register("water_loggable", () -> WaterLoggableCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockOffsetCondition>> OFFSET = REGISTRY.register("offset", () -> BlockOffsetCondition.CODEC);
}
