package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.*;
import com.iafenvoy.origins.data.condition.builtin.damage.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class DamageConditions {
    public static final DeferredRegister<MapCodec<? extends DamageCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.DAMAGE_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<AmountCondition>> AMOUNT = REGISTRY.register("amount", () -> AmountCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<AttackerDamageCondition>> ATTACKER = REGISTRY.register("attacker", () -> AttackerDamageCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageInTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> DamageInTagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageNameCondition>> NAME = REGISTRY.register("name", () -> DamageNameCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<ProjectileCondition>> PROJECTILE = REGISTRY.register("projectile", () -> ProjectileCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageTypeCondition>> TYPE = REGISTRY.register("type", () -> DamageTypeCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageAndCondition>> AND = REGISTRY.register("and", () -> DamageAndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageChanceCondition>> CHANCE = REGISTRY.register("chance", () -> DamageChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> DamageConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageNotCondition>> NOT = REGISTRY.register("not", () -> DamageNotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<DamageOrCondition>> OR = REGISTRY.register("or", () -> DamageOrCondition.CODEC);
}
