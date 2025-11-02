package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.NoOpAction;
import com.iafenvoy.origins.data.action.builtin.bientity.*;
import com.iafenvoy.origins.data.action.builtin.bientity.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BiEntityActions {
    public static final DeferredRegister<MapCodec<? extends BiEntityAction>> REGISTRY = DeferredRegister.create(ActionRegistries.BI_ENTITY_ACTION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<NoOpAction>> NO_OP = REGISTRY.register(Constants.NO_OP_KEY, () -> NoOpAction.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<AddVelocityAction>> ADD_VELOCITY = REGISTRY.register("add_velocity", () -> AddVelocityAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<DamageTargetAction>> DAMAGE_TARGET = REGISTRY.register("damage_target", () -> DamageTargetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<MountAction>> MOUNT = REGISTRY.register("mount", () -> MountAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<SetInLoveAction>> SET_IN_LOVE = REGISTRY.register("set_in_love", () -> SetInLoveAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<TameAction>> TAME = REGISTRY.register("tame", () -> TameAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<AndAction>> AND = REGISTRY.register("and", () -> AndAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<ChanceAction>> CHANCE = REGISTRY.register("chance", () -> ChanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<ChoiceAction>> CHOICE = REGISTRY.register("choice", () -> ChoiceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<DelayAction>> DELAY = REGISTRY.register("delay", () -> DelayAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<IfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> IfElseAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<IfElseListAction>> IF_ELSE_LIST = REGISTRY.register("if_else_list", () -> IfElseListAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<SideAction>> SIDE = REGISTRY.register("side", () -> SideAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<InvertAction>> INVERT = REGISTRY.register("invert", () -> InvertAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<SourceActionAction>> SOURCE_ACTION = REGISTRY.register("source_action", () -> SourceActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<TargetActionAction>> TARGET_ACTION = REGISTRY.register("target_action", () -> TargetActionAction.CODEC);
}
