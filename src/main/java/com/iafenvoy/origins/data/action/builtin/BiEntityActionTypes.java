package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.builtin.bientity.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BiEntityActionTypes {
    public static final DeferredRegister<MapCodec<? extends BiEntityAction>> REGISTRY = DeferredRegister.create(ActionRegistries.BI_ENTITY_ACTION, Origins.MOD_ID);
    //List
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<AddVelocityAction>> ADD_VELOCITY = REGISTRY.register("add_velocity", () -> AddVelocityAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<DamageTargetAction>> DAMAGE = REGISTRY.register("damage_target", () -> DamageTargetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<MountAction>> MOUNT = REGISTRY.register("mount", () -> MountAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<SetInLoveAction>> SET_IN_LOVE = REGISTRY.register("set_in_love", () -> SetInLoveAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<TameAction>> TAME = REGISTRY.register("tame", () -> TameAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<InvertAction>> INVERT = REGISTRY.register("invert", () -> InvertAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<SourceActionAction>> ACTOR_ACTION = REGISTRY.register("source_action", () -> SourceActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<TargetActionAction>> TARGET_ACTION = REGISTRY.register("target_action", () -> TargetActionAction.CODEC);
}
