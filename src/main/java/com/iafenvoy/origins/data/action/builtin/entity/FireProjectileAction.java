package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.MiscUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record FireProjectileAction(EntityType<?> entityType, float divergence, float speed, int count,
                                   CompoundTag tag, EntityAction projectileAction) implements EntityAction {
    public static final MapCodec<FireProjectileAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(FireProjectileAction::entityType),
            Codec.FLOAT.optionalFieldOf("divergence", 1F).forGetter(FireProjectileAction::divergence),
            Codec.FLOAT.optionalFieldOf("speed", 1F).forGetter(FireProjectileAction::speed),
            Codec.INT.optionalFieldOf("count", 1).forGetter(FireProjectileAction::count),
            CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(FireProjectileAction::tag),
            EntityAction.optionalCodec("projectile_action").forGetter(FireProjectileAction::projectileAction)
    ).apply(i, FireProjectileAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (!(source.level() instanceof ServerLevel serverWorld)) return;

        RandomSource random = serverWorld.getRandom();

        Vec3 velocity = source.getDeltaMovement();
        Vec3 verticalOffset = source.position().add(0, source.getEyeHeight(source.getPose()), 0);

        float pitch = source.getXRot();
        float yaw = source.getYRot();

        for (int i = 0; i < this.count; i++) {
            Entity entityToSpawn = MiscUtil
                    .getEntityWithPassengers(serverWorld, this.entityType, this.tag, verticalOffset, yaw, pitch)
                    .orElse(null);
            if (entityToSpawn == null) return;

            if (entityToSpawn instanceof Projectile projectileToSpawn) {
                if (projectileToSpawn instanceof AbstractHurtingProjectile explosiveProjectileToSpawn)
                    explosiveProjectileToSpawn.accelerationPower = this.speed;
                projectileToSpawn.setOwner(source);
                projectileToSpawn.shootFromRotation(source, pitch, yaw, 0F, this.speed, this.divergence);
            } else {
                float j = 0.017453292F;
                double k = 0.0075;

                float l = -Mth.sin(yaw * j) * Mth.cos(pitch * j);
                float m = -Mth.sin(pitch * j);
                float n = Mth.cos(yaw * j) * Mth.cos(pitch * j);

                Vec3 velocityToApply = new Vec3(l, m, n)
                        .normalize()
                        .add(random.nextGaussian() * k * this.divergence, random.nextGaussian() * k * this.divergence, random.nextGaussian() * k * this.divergence)
                        .scale(this.speed);
                entityToSpawn.setDeltaMovement(velocityToApply);
                entityToSpawn.push(velocity.x, source.onGround() ? 0.0D : velocity.y, velocity.z);
            }

            if (!this.tag.isEmpty()) {
                CompoundTag mergedNbt = entityToSpawn.saveWithoutId(new CompoundTag());
                mergedNbt.merge(this.tag);
                entityToSpawn.load(mergedNbt);
            }

            serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
            this.projectileAction.execute(entityToSpawn);
        }
    }
}
