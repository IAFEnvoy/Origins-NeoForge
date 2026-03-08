package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record InventoryPower(String title, boolean dropOnDeath, int containerSize,
                             EntityCondition condition) implements Power {
    public static final MapCodec<InventoryPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("title", "container.inventory").forGetter(InventoryPower::title),
            Codec.BOOL.optionalFieldOf("drop_on_death", false).forGetter(InventoryPower::dropOnDeath),
            Codec.INT.optionalFieldOf("container_size", 9).forGetter(InventoryPower::containerSize),
            EntityCondition.optionalCodec("condition").forGetter(InventoryPower::condition)
    ).apply(i, InventoryPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
