package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.builtin.block.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BlockActionTypes {
    public static final DeferredRegister<MapCodec<? extends BlockAction>> REGISTRY = DeferredRegister.create(ActionRegistries.BLOCK_ACTION, Origins.MOD_ID);
    //List
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BonemealAction>> BONEMEAL = REGISTRY.register("bonemeal", () -> BonemealAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<ExecuteCommandAction>> EXECUTE_COMMAND = REGISTRY.register("execute_command", () -> ExecuteCommandAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<ExplodeAction>> EXPLODE = REGISTRY.register("explode", () -> ExplodeAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<SetBlockAction>> SET_BLOCK = REGISTRY.register("set_block", () -> SetBlockAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<SpawnEntityAction>> SPAWN_ENTITY = REGISTRY.register("spawn_entity", () -> SpawnEntityAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<AbsoluteOffsetAction>> ABSOLUTE_OFFSET = REGISTRY.register("absolute_offset", () -> AbsoluteOffsetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockRegionApplyAction>> REGION_APPLY = REGISTRY.register("region_apply", () -> BlockRegionApplyAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<RelativeOffsetAction>> RELATIVE_OFFSET = REGISTRY.register("relative_offset", () -> RelativeOffsetAction.CODEC);
}
