package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public record FluidHeightCondition(FluidType fluid, Comparison comparison,
                                   double compareTo) implements EntityCondition {
    public static final MapCodec<FluidHeightCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            NeoForgeRegistries.FLUID_TYPES.byNameCodec().fieldOf("fluid").forGetter(FluidHeightCondition::fluid),
            Comparison.CODEC.fieldOf("comparison").forGetter(FluidHeightCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(FluidHeightCondition::compareTo)
    ).apply(i, FluidHeightCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.comparison.compare(entity.getFluidTypeHeight(this.fluid), this.compareTo);
    }
}
