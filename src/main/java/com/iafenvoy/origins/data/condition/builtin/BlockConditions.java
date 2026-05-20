package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.builtin.block.*;
import com.iafenvoy.origins.data.condition.builtin.block.meta.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Objects;

import static com.iafenvoy.origins.data.condition.SimpleConditions.createBlock;

@SuppressWarnings("unused")
public final class BlockConditions {
    public static final DeferredRegister<MapCodec<? extends BlockCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.BLOCK_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<AdjacentCondition>> ADJACENT = REGISTRY.register("adjacent", () -> AdjacentCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> ATTACHABLE = REGISTRY.register("attachable", () -> createBlock((level, pos) -> Arrays.stream(Direction.values()).anyMatch(d -> level.getBlockState(pos.relative(d)).isFaceSturdy(level, pos, d.getOpposite()))));
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlastResistanceCondition>> BLAST_RESISTANCE = REGISTRY.register("blast_resistance", () -> BlastResistanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> BLOCK_ENTITY = REGISTRY.register("block_entity", () -> createBlock((level, pos) -> Objects.nonNull(level.getBlockEntity(pos))));
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockIdCondition>> BLOCK_ID = REGISTRY.register("block_id", () -> BlockIdCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<BlockStateCondition>> BLOCK_STATE = REGISTRY.register("block_state", () -> BlockStateCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<DistanceFromCoordinatesCondition>> DISTANCE_FROM_COORDINATES = REGISTRY.register("distance_from_coordinates", () -> DistanceFromCoordinatesCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> EXPOSED_TO_SKY = REGISTRY.register("exposed_to_sky", () -> createBlock(BlockAndTintGetter::canSeeSky));
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<FluidIdCondition>> FLUID = REGISTRY.register("fluid", () -> FluidIdCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<HardnessCondition>> HARDNESS = REGISTRY.register("hardness", () -> HardnessCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<HeightCondition>> HEIGHT = REGISTRY.register("height", () -> HeightCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> InTagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> LIGHT_BLOCKING = REGISTRY.register("light_blocking", () -> createBlock((level, pos) -> level.getBlockState(pos).canOcclude()));
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<LightLevelCondition>> LIGHT_LEVEL = REGISTRY.register("light_level", () -> LightLevelCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<MovementBlockingCondition>> MOVEMENT_BLOCKING = REGISTRY.register("movement_blocking", () -> MovementBlockingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<NbtCondition>> NBT = REGISTRY.register("nbt", () -> NbtCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> REPLACEABLE = REGISTRY.register("replaceable", () -> createBlock((level, pos) -> level.getBlockState(pos).canBeReplaced()));
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<SlipperinessCondition>> SLIPPERINESS = REGISTRY.register("slipperiness", () -> SlipperinessCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<? extends BlockCondition>> WATER_LOGGABLE = REGISTRY.register("water_loggable", () -> createBlock((level, pos) -> level.getBlockState(pos).getBlock() instanceof LiquidBlockContainer));
    //Meta
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register("chance", () -> ChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> ConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<OffsetCondition>> OFFSET = REGISTRY.register("offset", () -> OffsetCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
