package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.EmptyCondition;
import com.iafenvoy.origins.data.condition.builtin.bientity.*;
import com.iafenvoy.origins.data.condition.builtin.bientity.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class BiEntityConditions {
    public static final DeferredRegister<MapCodec<? extends BiEntityCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BI_ENTITY_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<EmptyCondition>> EMPTY = REGISTRY.register("empty", () -> EmptyCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<AttackerCondition>> ATTACKER = REGISTRY.register("attacker", () -> AttackerCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<AttackTargetCondition>> ATTACK_TARGET = REGISTRY.register("attack_target", () -> AttackTargetCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<CanSeeCondition>> CAN_SEE = REGISTRY.register("can_see", () -> CanSeeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<OwnerCondition>> OWNER = REGISTRY.register("owner", () -> OwnerCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<RidingCondition>> RIDING = REGISTRY.register("riding", () -> RidingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<RidingRecursiveCondition>> RIDING_RECURSIVE = REGISTRY.register("riding_recursive", () -> RidingRecursiveCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<RidingRootCondition>> RIDING_ROOT = REGISTRY.register("riding_root", () -> RidingRootCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<ActorConditionCondition>> ACTOR_CONDITION = REGISTRY.register("actor_condition", () -> ActorConditionCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<BothCondition>> BOTH = REGISTRY.register("both", () -> BothCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<EitherCondition>> EITHER = REGISTRY.register("either", () -> EitherCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<EqualCondition>> EQUAL = REGISTRY.register("equal", () -> EqualCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<InvertCondition>> INVERT = REGISTRY.register("invert", () -> InvertCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<TargetConditionCondition>> TARGET_CONDITION = REGISTRY.register("target_condition", () -> TargetConditionCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<UndirectedCondition>> UNDIRECTED = REGISTRY.register("undirected", () -> UndirectedCondition.CODEC);
}
