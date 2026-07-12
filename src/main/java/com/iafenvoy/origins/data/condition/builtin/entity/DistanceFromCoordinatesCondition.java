package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data._common.helper.DistanceFromCoordinatesHelper;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.iafenvoy.origins.util.math.Comparison;
import com.iafenvoy.origins.util.math.ReferencePoint;
import com.iafenvoy.origins.util.math.Shape;
import com.iafenvoy.origins.util.wrapper.OptionalBoolean;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;

public record DistanceFromCoordinatesCondition(ReferencePoint reference, Optional<Vec3> offset, boolean ignoreX,
                                               boolean ignoreY, boolean ignoreZ, Shape shape,
                                               boolean scaleReferenceToDimension,
                                               OptionalBoolean resultOnWrongDimension, OptionalInt roundToDigit,
                                               Comparison comparison) implements EntityCondition, DistanceFromCoordinatesHelper {
    public static final MapCodec<DistanceFromCoordinatesCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ReferencePoint.CODEC.optionalFieldOf("reference", ReferencePoint.WORLD_ORIGIN).forGetter(DistanceFromCoordinatesCondition::reference),
            Vec3.CODEC.optionalFieldOf("offset").forGetter(DistanceFromCoordinatesCondition::offset),
            Codec.BOOL.optionalFieldOf("ignore_x", false).forGetter(DistanceFromCoordinatesCondition::ignoreX),
            Codec.BOOL.optionalFieldOf("ignore_y", false).forGetter(DistanceFromCoordinatesCondition::ignoreY),
            Codec.BOOL.optionalFieldOf("ignore_z", false).forGetter(DistanceFromCoordinatesCondition::ignoreZ),
            Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(DistanceFromCoordinatesCondition::shape),
            Codec.BOOL.optionalFieldOf("scale_reference_to_dimension", true).forGetter(DistanceFromCoordinatesCondition::scaleReferenceToDimension),
            MiscCodecs.bool("result_on_wrong_dimension").forGetter(DistanceFromCoordinatesCondition::resultOnWrongDimension),
            MiscCodecs.integer("round_to_digit").forGetter(DistanceFromCoordinatesCondition::roundToDigit),
            Comparison.CODEC.forGetter(DistanceFromCoordinatesCondition::comparison)
    ).apply(instance, DistanceFromCoordinatesCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.testDistanceFromCoordinates(entity.level(), entity.position());
    }
}
