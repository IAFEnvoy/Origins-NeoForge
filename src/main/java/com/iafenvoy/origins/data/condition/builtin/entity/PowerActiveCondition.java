package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record PowerActiveCondition(Holder<Power> power) implements EntityCondition {
    public static final MapCodec<PowerActiveCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Power.CODEC.fieldOf("power").forGetter(PowerActiveCondition::power)
    ).apply(i, PowerActiveCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.power.value().isActive(OriginDataHolder.get(entity));
    }
}
