package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.NoOpAction;
import com.iafenvoy.origins.data.action.builtin.block.*;
import com.iafenvoy.origins.data.action.builtin.block.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BlockActions {
    public static final DeferredRegister<MapCodec<? extends BlockAction>> REGISTRY = DeferredRegister.create(ActionRegistries.BLOCK_ACTION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<NoOpAction>> NO_OP = REGISTRY.register(Constants.NO_OP_KEY, () -> NoOpAction.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<BonemealAction>> BONEMEAL = REGISTRY.register("bonemeal", () -> BonemealAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<ExecuteCommandAction>> EXECUTE_COMMAND = REGISTRY.register("execute_command", () -> ExecuteCommandAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<ExplodeAction>> EXPLODE = REGISTRY.register("explode", () -> ExplodeAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<SetBlockAction>> SET_BLOCK = REGISTRY.register("set_block", () -> SetBlockAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<SpawnEntityAction>> SPAWN_ENTITY = REGISTRY.register("spawn_entity", () -> SpawnEntityAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<AbsoluteOffsetAction>> ABSOLUTE_OFFSET = REGISTRY.register("absolute_offset", () -> AbsoluteOffsetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<AndAction>> AND = REGISTRY.register("and", () -> AndAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<ChanceAction>> CHANCE = REGISTRY.register("chance", () -> ChanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<ChoiceAction>> CHOICE = REGISTRY.register("choice", () -> ChoiceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<DelayAction>> DELAY = REGISTRY.register("delay", () -> DelayAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<IfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> IfElseAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<IfElseListAction>> IF_ELSE_LIST = REGISTRY.register("if_else_list", () -> IfElseListAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<RegionApplyAction>> REGION_APPLY = REGISTRY.register("region_apply", () -> RegionApplyAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<RelativeOffsetAction>> RELATIVE_OFFSET = REGISTRY.register("relative_offset", () -> RelativeOffsetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<SideAction>> SIDE = REGISTRY.register("side", () -> SideAction.CODEC);
}
