package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record AreaOfEffectAction(float radius, BiEntityAction biEntityAction,
                                 BiEntityCondition biEntityCondition, boolean includeTarget) implements EntityAction {
    public static final MapCodec<AreaOfEffectAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("radius", 16.0f).forGetter(AreaOfEffectAction::radius),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(AreaOfEffectAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(AreaOfEffectAction::biEntityCondition),
            Codec.BOOL.optionalFieldOf("include_target", false).forGetter(AreaOfEffectAction::includeTarget)
    ).apply(i, AreaOfEffectAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        AABB area = source.getBoundingBox().inflate(this.radius);
        float squareRadius = this.radius * this.radius;
        for (Entity target : source.level().getEntitiesOfClass(Entity.class, area, EntitySelector.NO_SPECTATORS)) {
            if (target == source && !this.includeTarget) continue;
            double dx = target.getX() - source.getX();
            double dy = target.getY() - source.getY();
            double dz = target.getZ() - source.getZ();
            if (dx * dx + dy * dy + dz * dz > squareRadius) continue;
            if (!this.biEntityCondition.test(source, target)) continue;
            this.biEntityAction.execute(source, target);
        }
    }
}
