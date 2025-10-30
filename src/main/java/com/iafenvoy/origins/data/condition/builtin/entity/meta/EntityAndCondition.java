package com.iafenvoy.origins.data.condition.builtin.entity.meta;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EntityAndCondition(List<EntityCondition> conditions) implements EntityCondition {
    public static final MapCodec<EntityAndCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.listOf().fieldOf("conditions").forGetter(EntityAndCondition::conditions)
    ).apply(i, EntityAndCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.conditions.stream().allMatch(x -> x.test(entity));
    }
}
