package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.LevelUtil;
import com.iafenvoy.origins.util.codec.OptionalCodecs;
import com.iafenvoy.origins.util.math.Comparison;
import com.iafenvoy.origins.util.math.ReferencePoint;
import com.iafenvoy.origins.util.math.Shape;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.OptionalInt;

public record DistanceFromCoordinatesCondition(ReferencePoint reference, Optional<Vec3> offset, boolean ignoreX,
                                               boolean ignoreY, boolean ignoreZ, Shape shape,
                                               boolean scaleReferenceToDimension,
                                               Optional<Boolean> resultOnWrongDimension, OptionalInt roundToDigit,
                                               Comparison comparison) implements BlockCondition, LevelUtil.CoordinatesConditionDataGetter {
    public static final MapCodec<DistanceFromCoordinatesCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ReferencePoint.CODEC.optionalFieldOf("reference", ReferencePoint.WORLD_ORIGIN).forGetter(DistanceFromCoordinatesCondition::reference),
            Vec3.CODEC.optionalFieldOf("offset").forGetter(DistanceFromCoordinatesCondition::offset),
            Codec.BOOL.optionalFieldOf("ignore_x", false).forGetter(DistanceFromCoordinatesCondition::ignoreX),
            Codec.BOOL.optionalFieldOf("ignore_y", false).forGetter(DistanceFromCoordinatesCondition::ignoreY),
            Codec.BOOL.optionalFieldOf("ignore_z", false).forGetter(DistanceFromCoordinatesCondition::ignoreZ),
            Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(DistanceFromCoordinatesCondition::shape),
            Codec.BOOL.optionalFieldOf("scale_reference_to_dimension", true).forGetter(DistanceFromCoordinatesCondition::scaleReferenceToDimension),
            Codec.BOOL.optionalFieldOf("result_on_wrong_dimension").forGetter(DistanceFromCoordinatesCondition::resultOnWrongDimension),
            OptionalCodecs.integer("round_to_digit").forGetter(DistanceFromCoordinatesCondition::roundToDigit),
            Comparison.CODEC.forGetter(DistanceFromCoordinatesCondition::comparison)
    ).apply(instance, DistanceFromCoordinatesCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        return LevelUtil.testDistanceFromCoordinates(this, level, Vec3.atCenterOf(pos));
    }
}
