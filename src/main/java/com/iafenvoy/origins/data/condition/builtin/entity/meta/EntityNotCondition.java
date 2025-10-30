package com.iafenvoy.origins.data.condition.builtin.entity.meta;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public record EntityNotCondition(EntityCondition condition) implements EntityCondition {
    public static final MapCodec<EntityNotCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.fieldOf("condition").forGetter(EntityNotCondition::new)
    ).apply(i, EntityNotCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return !this.condition.test(entity);
    }
}
