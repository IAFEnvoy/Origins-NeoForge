package com.iafenvoy.origins.data.common;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

public record RaycastSettings(double distance, boolean block, boolean entity, ClipContext.Block shapeType,
                              ClipContext.Fluid fluidHandling) {
    public static final MapCodec<RaycastSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("distance").forGetter(RaycastSettings::distance),
            Codec.BOOL.optionalFieldOf("block", true).forGetter(RaycastSettings::block),
            Codec.BOOL.optionalFieldOf("entity", true).forGetter(RaycastSettings::entity),
            ExtraEnumCodecs.CLIP_CONTEXT_BLOCK.optionalFieldOf("shape_type", ClipContext.Block.OUTLINE).forGetter(RaycastSettings::shapeType),
            ExtraEnumCodecs.CLIP_CONTEXT_FLUID.optionalFieldOf("fluid_handling", ClipContext.Fluid.ANY).forGetter(RaycastSettings::fluidHandling)
    ).apply(instance, RaycastSettings::new));

    @NotNull
    public HitResult perform(@NotNull Entity entity, BiEntityCondition entityValidator) {
        return this.perform(entity, new Vec3(entity.getX(), entity.getEyeY(), entity.getZ()), entity.getViewVector(1), entityValidator);
    }

    @NotNull
    public HitResult perform(@NotNull Entity entity, @NotNull Vec3 origin, @NotNull Vec3 direction, BiEntityCondition entityValidator) {
        Vec3 target = origin.add(direction.normalize().scale(this.distance()));
        HitResult result = null;
        if (this.entity())
            result = this.performEntityRaycast(entity, origin, target, entityValidator);
        if (this.block()) {
            BlockHitResult blockHit = this.performBlockRaycast(entity, origin, target);
            if (blockHit.getType() != HitResult.Type.MISS) {
                if (result == null || result.getType() == HitResult.Type.MISS || result.distanceTo(entity) > blockHit.distanceTo(entity))
                    result = blockHit;
            }
        }
        if (result == null)
            return BlockHitResult.miss(origin, Direction.getNearest(direction.x, direction.y, direction.z), new BlockPos((int) target.x(), (int) target.y(), (int) target.z()));
        return result;
    }

    private BlockHitResult performBlockRaycast(Entity source, Vec3 origin, Vec3 target) {
        ClipContext context = new ClipContext(origin, target, this.shapeType(), this.fluidHandling(), source);
        return source.level().clip(context);
    }

    private EntityHitResult performEntityRaycast(Entity source, Vec3 origin, Vec3 target, BiEntityCondition biEntityCondition) {
        Vec3 ray = target.subtract(origin);
        AABB box = source.getBoundingBox().expandTowards(ray).inflate(1.0D, 1.0D, 1.0D);
        return ProjectileUtil.getEntityHitResult(source, origin, target, box, (entityx) -> !entityx.isSpectator() && biEntityCondition.test(source, entityx), ray.lengthSqr());
    }
}

