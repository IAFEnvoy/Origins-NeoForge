package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public record EntityRegionApplyAction(double radius, Shape shape, BiEntityAction biEntityAction,
                                      Optional<BiEntityCondition> biEntityCondition,
                                      boolean includeActor) implements EntityAction {
    public static final MapCodec<EntityRegionApplyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.DOUBLE.optionalFieldOf("radius", 16.0).forGetter(EntityRegionApplyAction::radius),
            Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(EntityRegionApplyAction::shape),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(EntityRegionApplyAction::biEntityAction),
            BiEntityCondition.CODEC.optionalFieldOf("bientity_condition").forGetter(EntityRegionApplyAction::biEntityCondition),
            Codec.BOOL.optionalFieldOf("includeActor", false).forGetter(EntityRegionApplyAction::includeActor)
    ).apply(i, EntityRegionApplyAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        for (Entity target : this.shape.getProcessor().apply(source.level(), source.position(), this.radius)) {
            if (target == source && !this.includeActor) continue;
            if (this.biEntityCondition.isPresent() && !this.biEntityCondition.get().test(source, target)) continue;
            this.biEntityAction.accept(source, target);
        }
    }

    //FIXME::Share enum
    public enum Shape implements StringRepresentable {
        CUBE((level, center, radius) -> level.getEntitiesOfClass(Entity.class, createArea(center, radius))),
        STAR((level, center, radius) -> level.getEntitiesOfClass(Entity.class, createArea(center, radius), EntitySelector.NO_SPECTATORS.and(entity -> Math.abs(entity.getX() - center.x) + Math.abs(entity.getY() - center.y) + Math.abs(entity.getZ() - center.z) <= radius))),
        SPHERE((level, center, radius) -> level.getEntitiesOfClass(Entity.class, createArea(center, radius), EntitySelector.NO_SPECTATORS.and(entity -> entity.distanceToSqr(center) <= radius * radius)));
        public static final Codec<Shape> CODEC = StringRepresentable.fromEnum(Shape::values);
        private final TriFunction<Level, Vec3, Double, List<Entity>> processor;

        Shape(TriFunction<Level, Vec3, Double, List<Entity>> processor) {
            this.processor = processor;
        }

        public TriFunction<Level, Vec3, Double, List<Entity>> getProcessor() {
            return this.processor;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        private static AABB createArea(Vec3 pos, double r) {
            return new AABB(pos.subtract(r, r, r), pos.add(r, r, r));
        }
    }
}
