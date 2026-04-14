package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public enum Shape implements StringRepresentable {
    CUBE((x, y, z) -> Math.max(Math.max(x, y), z)),
    STAR((x, y, z) -> Math.abs(x) + Math.abs(y) + Math.abs(z)),
    SPHERE((x, y, z) -> Math.sqrt(x * x + y * y + z * z));
    public static final Codec<Shape> CODEC = StringRepresentable.fromEnum(Shape::values);
    private final DistanceCalculator calculator;

    Shape(DistanceCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public double getDistance(double xDistance, double yDistance, double zDistance) {
        return this.calculator.calculate(xDistance, yDistance, zDistance);
    }

    public boolean isInRange(double xDistance, double yDistance, double zDistance, double radius) {
        return this.calculator.calculate(xDistance, yDistance, zDistance) <= radius;
    }

    public List<BlockPos> getBlocks(BlockPos center, int radius) {
        List<BlockPos> results = new LinkedList<>();
        for (int i = -radius; i <= radius; i++)
            for (int j = -radius; j <= radius; j++)
                for (int k = -radius; k <= radius; k++)
                    if (this.isInRange(i, j, k, radius))
                        results.add(center.offset(i, j, k));
        return results;
    }

    public List<Entity> getEntities(Level level, Vec3 center, double radius) {
        return level.getEntitiesOfClass(Entity.class, createArea(center, radius), EntitySelector.NO_SPECTATORS.and(entity -> this.isInRange(entity.getX(), entity.getY(), entity.getZ(), radius)));
    }

    private static AABB createArea(Vec3 pos, double r) {
        return new AABB(pos.subtract(r, r, r), pos.add(r, r, r));
    }

    @FunctionalInterface
    private interface DistanceCalculator {
        double calculate(double x, double y, double z);
    }
}
