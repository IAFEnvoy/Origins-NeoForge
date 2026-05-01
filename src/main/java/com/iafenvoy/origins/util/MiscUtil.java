package com.iafenvoy.origins.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MiscUtil {
    public static Vec3 getPoseDependentEyePos(Entity entity) {
        return new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static InteractionResult reduce(InteractionResult first, InteractionResult second) {
        return second.consumesAction() && !first.consumesAction() || second.shouldSwing() && !first.shouldSwing() ? second : first;
    }

    public static Optional<Entity> getEntityWithPassengers(Level level, EntityType<?> entityType, @Nullable CompoundTag entityNbt, Vec3 pos, float yaw, float pitch) {
        if (!(level instanceof ServerLevel serverLevel)) return Optional.empty();
        CompoundTag entityToSpawnNbt = new CompoundTag();
        if (entityNbt != null) entityToSpawnNbt.merge(entityNbt);
        entityToSpawnNbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());

        Entity entityToSpawn = EntityType.loadEntityRecursive(entityToSpawnNbt, serverLevel, entity -> {
            entity.moveTo(pos.x, pos.y, pos.z, yaw, pitch);
            return entity;
        });
        if (entityToSpawn == null) return Optional.empty();

        if (entityNbt == null && entityToSpawn instanceof Mob mobToSpawn) EventHooks.finalizeMobSpawn(
                mobToSpawn,
                serverLevel,
                serverLevel.getCurrentDifficultyAt(BlockPos.containing(pos)),
                MobSpawnType.COMMAND,
                null
        );
        return Optional.of(entityToSpawn);

    }
}
