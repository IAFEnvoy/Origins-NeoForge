package com.iafenvoy.origins.util;

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
    CUBE((x, y, z, radius) -> Math.abs(x) <= radius && Math.abs(y) <= radius && Math.abs(z) <= radius),
    STAR((x, y, z, radius) -> Math.abs(x) + Math.abs(y) + Math.abs(z) <= radius),
    SPHERE((x, y, z, radius) -> x * x + y * y + z * z <= radius * radius);
    public static final Codec<Shape> CODEC = StringRepresentable.fromEnum(Shape::values);
    private final DistancePredicate processor;

    Shape(DistancePredicate processor) {
        this.processor = processor;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public List<BlockPos> getBlocks(BlockPos center, int radius) {
        List<BlockPos> results = new LinkedList<>();
        for (int i = -radius; i <= radius; i++)
            for (int j = -radius; j <= radius; j++)
                for (int k = -radius; k <= radius; k++)
                    if (this.processor.test(i, j, k, radius))
                        results.add(center.offset(i, j, k));
        return results;
    }

    public List<Entity> getEntities(Level level, Vec3 center, double radius) {
        return level.getEntitiesOfClass(Entity.class, createArea(center, radius), EntitySelector.NO_SPECTATORS.and(entity -> this.processor.test(entity.getX(), entity.getY(), entity.getZ(), radius)));
    }

    private static AABB createArea(Vec3 pos, double r) {
        return new AABB(pos.subtract(r, r, r), pos.add(r, r, r));
    }

    @FunctionalInterface
    private interface DistancePredicate {
        boolean test(double x, double y, double z, double radius);
    }
}
