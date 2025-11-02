package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.Shape;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record RegionApplyAction(double radius, Shape shape, BiEntityAction biEntityAction,
                                BiEntityCondition biEntityCondition, boolean includeActor) implements EntityAction {
    public static final MapCodec<RegionApplyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.DOUBLE.optionalFieldOf("radius", 16.0).forGetter(RegionApplyAction::radius),
            Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(RegionApplyAction::shape),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(RegionApplyAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(RegionApplyAction::biEntityCondition),
            Codec.BOOL.optionalFieldOf("includeActor", false).forGetter(RegionApplyAction::includeActor)
    ).apply(i, RegionApplyAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        for (Entity target : this.shape.getEntities(source.level(), source.position(), this.radius)) {
            if (target == source && !this.includeActor) continue;
            if (!this.biEntityCondition.test(source, target)) continue;
            this.biEntityAction.execute(source, target);
        }
    }
}
