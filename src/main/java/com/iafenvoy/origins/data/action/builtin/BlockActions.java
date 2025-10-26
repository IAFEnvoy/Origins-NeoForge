package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EmptyAction;
import com.iafenvoy.origins.data.action.builtin.block.*;
import com.iafenvoy.origins.data.action.builtin.block.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BlockActions {
    public static final DeferredRegister<MapCodec<? extends BlockAction>> REGISTRY = DeferredRegister.create(ActionRegistries.BLOCK_ACTION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<EmptyAction>> EMPTY = REGISTRY.register("empty", () -> EmptyAction.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BonemealAction>> BONEMEAL = REGISTRY.register("bonemeal", () -> BonemealAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<ExecuteCommandAction>> EXECUTE_COMMAND = REGISTRY.register("execute_command", () -> ExecuteCommandAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockExplodeAction>> EXPLODE = REGISTRY.register("explode", () -> BlockExplodeAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<SetBlockAction>> SET_BLOCK = REGISTRY.register("set_block", () -> SetBlockAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<SpawnEntityAction>> SPAWN_ENTITY = REGISTRY.register("spawn_entity", () -> SpawnEntityAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<AbsoluteOffsetAction>> ABSOLUTE_OFFSET = REGISTRY.register("absolute_offset", () -> AbsoluteOffsetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockAndAction>> AND = REGISTRY.register("and", () -> BlockAndAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockChanceAction>> CHANCE = REGISTRY.register("chance", () -> BlockChanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockChoiceAction>> CHOICE = REGISTRY.register("choice", () -> BlockChoiceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockDelayAction>> DELAY = REGISTRY.register("delay", () -> BlockDelayAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockIfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> BlockIfElseAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockIfElseListAction>> IF_ELSE_LIST = REGISTRY.register("if_else_list", () -> BlockIfElseListAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockNothingAction>> NOTHING = REGISTRY.register("nothing", () -> BlockNothingAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockRegionApplyAction>> REGION_APPLY = REGISTRY.register("region_apply", () -> BlockRegionApplyAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BlockSideAction>> SIDE = REGISTRY.register("side", () -> BlockSideAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<RelativeOffsetAction>> RELATIVE_OFFSET = REGISTRY.register("relative_offset", () -> RelativeOffsetAction.CODEC);
}
