package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record FluidHeightCondition(ResourceLocation fluid, String comparison,
                                   double compareTo) implements EntityCondition {
    public static final MapCodec<FluidHeightCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(FluidHeightCondition::fluid),
            Codec.STRING.optionalFieldOf("comparison", ">").forGetter(FluidHeightCondition::comparison),
            Codec.DOUBLE.optionalFieldOf("compare_to", 0.0).forGetter(FluidHeightCondition::compareTo)
    ).apply(i, FluidHeightCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        double fluidHeight = entity.getFluidHeight(net.minecraft.tags.FluidTags.WATER);
        return compare(fluidHeight, this.compareTo, this.comparison);
    }

    private static boolean compare(double a, double b, String op) {
        return switch (op) {
            case ">" -> a > b;
            case ">=" -> a >= b;
            case "<" -> a < b;
            case "<=" -> a <= b;
            case "==" -> a == b;
            case "!=" -> a != b;
            default -> false;
        };
    }
}
