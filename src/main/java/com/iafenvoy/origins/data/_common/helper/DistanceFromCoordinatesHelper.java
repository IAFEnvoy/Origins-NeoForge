package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.util.math.Comparison;
import com.iafenvoy.origins.util.math.ReferencePoint;
import com.iafenvoy.origins.util.math.Shape;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.OptionalInt;

public interface DistanceFromCoordinatesHelper {
    ReferencePoint reference();

    Optional<Vec3> offset();

    boolean ignoreX();

    boolean ignoreY();

    boolean ignoreZ();

    Shape shape();

    boolean scaleReferenceToDimension();

    Optional<Boolean> resultOnWrongDimension();

    OptionalInt roundToDigit();

    Comparison comparison();

    default boolean testDistanceFromCoordinates(Level level, Vec3 pos) {
        double scale = level.dimensionType().coordinateScale();
        Vec3 point = this.reference().getProcessor().apply(level, this.resultOnWrongDimension().isPresent());
        if (point == null) return this.resultOnWrongDimension().get();
        point = point.add(this.offset().orElse(Vec3.ZERO));
        if (this.scaleReferenceToDimension() && (point.x() != 0 || point.z() != 0)) {
            if (scale == 0) return this.comparison().compare(Double.POSITIVE_INFINITY);
            point = point.multiply(1 / scale, 1, 1 / scale);
        }
        Vec3 delta = point.subtract(pos);
        delta = new Vec3(this.ignoreX() ? 0 : Math.abs(delta.x()), this.ignoreY() ? 0 : Math.abs(delta.y()), this.ignoreZ() ? 0 : Math.abs(delta.z()));
        double distance = this.shape().getDistance(delta.x(), delta.y(), delta.z());
        if (this.roundToDigit().isPresent())
            distance = new BigDecimal(distance).setScale(this.roundToDigit().getAsInt(), RoundingMode.HALF_UP).doubleValue();
        return this.comparison().compare(distance);
    }
}
