package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ReferencePoint implements StringRepresentable {
    WORLD_SPAWN,
    WORLD_ORIGIN;
    public static final Codec<ReferencePoint> CODEC = StringRepresentable.fromValues(ReferencePoint::values);

    @Nullable
    public Vec3 getPoint(@NotNull Level level, boolean allowNull) {
        switch (this) {
            case WORLD_SPAWN:
                if (allowNull && level.dimension() != Level.OVERWORLD)
                    return null;
                LevelData data = level.getLevelData();
                BlockPos spawnPos = data.getSpawnPos();
                if (!level.getWorldBorder().isWithinBounds(spawnPos))
                    spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) level.getWorldBorder().getCenterX(), 0, (int) level.getWorldBorder().getCenterZ()));
                return new Vec3(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            case WORLD_ORIGIN:
            default:
                return Vec3.ZERO;
        }
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
