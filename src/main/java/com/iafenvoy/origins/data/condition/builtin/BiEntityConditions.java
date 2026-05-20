package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.builtin.bientity.*;
import com.iafenvoy.origins.data.condition.builtin.bientity.meta.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.OwnableEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

import static com.iafenvoy.origins.data.condition.SimpleConditions.createBiEntity;

@SuppressWarnings("unused")
public final class BiEntityConditions {
    public static final DeferredRegister<MapCodec<? extends BiEntityCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BI_ENTITY_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<? extends BiEntityCondition>> ATTACKER = REGISTRY.register("attacker", () -> createBiEntity((source, target) -> target instanceof LivingEntity living && Objects.equals(source, living.getLastHurtByMob())));
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<? extends BiEntityCondition>> ATTACK_TARGET = REGISTRY.register("attack_target", () -> createBiEntity((source, target) -> source instanceof Mob mob && Objects.equals(target, mob.getTarget()) || source instanceof NeutralMob n && Objects.equals(target, n.getTarget())));
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<CanSeeCondition>> CAN_SEE = REGISTRY.register("can_see", () -> CanSeeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<DistanceCondition>> DISTANCE = REGISTRY.register("distance", () -> DistanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<? extends BiEntityCondition>> EQUAL = REGISTRY.register("equal", () -> createBiEntity(Objects::equals));
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<InSetCondition>> IN_SET = REGISTRY.register("in_set", () -> InSetCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<? extends BiEntityCondition>> OWNER = REGISTRY.register("owner", () -> createBiEntity((source, target) -> target instanceof OwnableEntity ownable && Objects.equals(ownable.getOwnerUUID(), source.getUUID())));
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<RelativeRotationCondition>> RELATIVE_ROTATION = REGISTRY.register("relative_rotation", () -> RelativeRotationCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<? extends BiEntityCondition>> RIDING = REGISTRY.register("riding", () -> createBiEntity((source, target) -> Objects.equals(source.getVehicle(), target)));
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<RidingRecursiveCondition>> RIDING_RECURSIVE = REGISTRY.register("riding_recursive", () -> RidingRecursiveCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<? extends BiEntityCondition>> RIDING_ROOT = REGISTRY.register("riding_root", () -> createBiEntity((source, target) -> Objects.equals(source.getRootVehicle(), target)));
    //Meta
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<ActorConditionCondition>> ACTOR_CONDITION = REGISTRY.register("actor_condition", () -> ActorConditionCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<BothCondition>> BOTH = REGISTRY.register("both", () -> BothCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register("chance", () -> ChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> ConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<EitherCondition>> EITHER = REGISTRY.register("either", () -> EitherCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<InvertCondition>> INVERT = REGISTRY.register("invert", () -> InvertCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<TargetConditionCondition>> TARGET_CONDITION = REGISTRY.register("target_condition", () -> TargetConditionCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<UndirectedCondition>> UNDIRECTED = REGISTRY.register("undirected", () -> UndirectedCondition.CODEC);
}
