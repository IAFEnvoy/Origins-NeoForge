package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record PassengerRecursiveCondition(BiEntityCondition biEntityCondition,
                                          Comparison comparison) implements EntityCondition {
    public static final MapCodec<PassengerRecursiveCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(PassengerRecursiveCondition::biEntityCondition),
            Comparison.CODEC.forGetter(PassengerRecursiveCondition::comparison)
    ).apply(i, PassengerRecursiveCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        long matches = entity.getPassengers()
                .stream()
                .flatMap(Entity::getPassengersAndSelf)
                .filter(passenger -> this.biEntityCondition.test(passenger, entity))
                .count();
        return this.comparison.compare(matches);
    }
}
