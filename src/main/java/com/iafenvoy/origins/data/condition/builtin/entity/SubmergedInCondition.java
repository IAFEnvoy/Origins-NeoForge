package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record SubmergedInCondition(Fluid fluid) implements EntityCondition {
    public static final MapCodec<SubmergedInCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(SubmergedInCondition::fluid)
    ).apply(i, SubmergedInCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return Objects.equals(entity.getEyeInFluidType(), this.fluid.getFluidType());
    }
}
