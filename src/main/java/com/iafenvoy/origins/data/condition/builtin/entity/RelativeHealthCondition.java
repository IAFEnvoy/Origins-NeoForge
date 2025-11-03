package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record RelativeHealthCondition(Comparison comparison, double compareTo) implements EntityCondition {
    public static final MapCodec<RelativeHealthCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(RelativeHealthCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(RelativeHealthCondition::compareTo)
    ).apply(i, RelativeHealthCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof LivingEntity living && this.comparison.compare(living.getHealth() / living.getMaxHealth(), this.compareTo);
    }
}
