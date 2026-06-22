package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record CanSeeCondition(ClipContext.Block shapeType,
                              ClipContext.Fluid fluidHandling) implements BiEntityCondition {
    public static final MapCodec<CanSeeCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraEnumCodecs.CLIP_CONTEXT_BLOCK.optionalFieldOf("shape_type", ClipContext.Block.VISUAL).forGetter(CanSeeCondition::shapeType),
            ExtraEnumCodecs.CLIP_CONTEXT_FLUID.optionalFieldOf("fluid_handling", ClipContext.Fluid.NONE).forGetter(CanSeeCondition::fluidHandling)
    ).apply(i, CanSeeCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        if (!Objects.equals(source.level(), target.level())) {
            return false;
        } else {
            Vec3 vec3d = new Vec3(source.getX(), source.getEyeY(), source.getZ());
            Vec3 vec3d2 = new Vec3(target.getX(), target.getEyeY(), target.getZ());
            if (vec3d2.distanceTo(vec3d) > 128.0D) {
                return false;
            } else {
                return source.level().clip(new ClipContext(vec3d, vec3d2, this.shapeType, this.fluidHandling, source)).getType() == HitResult.Type.MISS;
            }
        }
    }
}
