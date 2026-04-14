package com.iafenvoy.origins.util;

import com.iafenvoy.origins.util.math.Comparison;
import com.iafenvoy.origins.util.math.ReferencePoint;
import com.iafenvoy.origins.util.math.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

public final class LevelUtil {
    public static boolean inSnow(Level world, BlockPos... blockPositions) {
        return Arrays.stream(blockPositions)
                .anyMatch(blockPos -> {
                    Biome biome = world.getBiome(blockPos).value();
                    return biome.getPrecipitationAt(blockPos) == Biome.Precipitation.SNOW
                            && isRainingAndExposed(world, blockPos);
                });
    }

    public static boolean inThunderstorm(Level world, BlockPos... blockPositions) {
        return Arrays.stream(blockPositions)
                .anyMatch(blockPos -> world.isThundering() && isRainingAndExposed(world, blockPos));
    }

    private static boolean isRainingAndExposed(Level world, BlockPos blockPos) {
        return world.isRaining()
                && world.canSeeSky(blockPos)
                && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() < blockPos.getY();
    }

    public static boolean testDistanceFromCoordinates(CoordinatesConditionDataGetter getter, Level level, Vec3 pos) {
        double scale = level.dimensionType().coordinateScale();
        Vec3 point = getter.reference().getPoint(level, getter.resultOnWrongDimension().isPresent());
        if (point == null)
            return getter.resultOnWrongDimension().get();
        point = point.add(getter.offset().orElse(Vec3.ZERO));
        if (getter.scaleReferenceToDimension() && (point.x() != 0 || point.z() != 0)) {
            if (scale == 0)
                return getter.comparison().compare(Double.POSITIVE_INFINITY);
            point = point.multiply(1 / scale, 1, 1 / scale);
        }
        Vec3 delta = point.subtract(pos);
        delta = new Vec3(getter.ignoreX() ? 0 : Math.abs(delta.x()), getter.ignoreY() ? 0 : Math.abs(delta.y()), getter.ignoreZ() ? 0 : Math.abs(delta.z()));
        double distance = getter.shape().getDistance(delta.x(), delta.y(), delta.z());
        if (getter.roundToDigit().isPresent())
            distance = new BigDecimal(distance).setScale(getter.roundToDigit().getAsInt(), RoundingMode.HALF_UP).doubleValue();
        return getter.comparison().compare(distance);
    }

    public interface CoordinatesConditionDataGetter {
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
    }
}
