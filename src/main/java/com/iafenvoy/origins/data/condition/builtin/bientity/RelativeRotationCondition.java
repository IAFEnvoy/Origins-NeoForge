package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public record RelativeRotationCondition(EnumSet<Direction.Axis> axis, RotationType actorRotation,
                                        RotationType targetRotation,
                                        Comparison comparison) implements BiEntityCondition {
    public static final MapCodec<RelativeRotationCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Direction.Axis.CODEC.listOf().xmap(EnumSet::copyOf, List::copyOf).optionalFieldOf("axis", EnumSet.allOf(Direction.Axis.class)).forGetter(RelativeRotationCondition::axis),
            RotationType.CODEC.optionalFieldOf("actor_rotation", RotationType.HEAD).forGetter(RelativeRotationCondition::actorRotation),
            RotationType.CODEC.optionalFieldOf("target_rotation", RotationType.BODY).forGetter(RelativeRotationCondition::targetRotation),
            Comparison.CODEC.forGetter(RelativeRotationCondition::comparison)
    ).apply(instance, RelativeRotationCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        Vec3 vec0 = this.actorRotation().getRotation(source);
        Vec3 vec1 = this.targetRotation().getRotation(target);
        vec0 = reduceAxes(vec0, this.axis());
        vec1 = reduceAxes(vec1, this.axis());
        double angle = getAngleBetween(vec0, vec1);
        return this.comparison.compare(angle);
    }

    private static double getAngleBetween(Vec3 a, Vec3 b) {
        double dot = a.dot(b);
        return dot / (a.length() * b.length());
    }

    private static Vec3 reduceAxes(Vec3 vector, EnumSet<Direction.Axis> axesToKeep) {
        return new Vec3(
                axesToKeep.contains(Direction.Axis.X) ? vector.x() : 0,
                axesToKeep.contains(Direction.Axis.Y) ? vector.y() : 0,
                axesToKeep.contains(Direction.Axis.Z) ? vector.z() : 0
        );
    }

    private static Vec3 getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = Mth.cos(g);
        float i = Mth.sin(g);
        float j = Mth.cos(f);
        float k = Mth.sin(f);
        return new Vec3(i * j, -k, h * j);
    }

    public enum RotationType implements StringRepresentable {
        HEAD(e -> e.getViewVector(1.0F)),
        BODY(e -> {
            if (e instanceof LivingEntity l) return getRotationVector(0F, l.yBodyRot);
            else return e.getViewVector(1.0F);
        });
        public static final Codec<RotationType> CODEC = StringRepresentable.fromValues(RotationType::values);
        private final Function<Entity, Vec3> function;

        RotationType(Function<Entity, Vec3> function) {
            this.function = function;
        }

        public Vec3 getRotation(Entity entity) {
            return this.function.apply(entity);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
