package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class WalkOnFluidPower extends Power {
    public static final MapCodec<WalkOnFluidPower> CODEC= RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(WalkOnFluidPower::getFluid)
    ).apply(i, WalkOnFluidPower::new));
    private final Fluid fluid;

    protected WalkOnFluidPower(BaseSettings settings, Fluid fluid) {
        super(settings);
        this.fluid = fluid;
    }

    public Fluid getFluid() {
        return this.fluid;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
