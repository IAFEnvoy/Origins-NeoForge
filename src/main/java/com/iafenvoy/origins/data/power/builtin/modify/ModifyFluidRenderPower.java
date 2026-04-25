package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.render.LevelRenderHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class ModifyFluidRenderPower extends Power {
    public static final MapCodec<ModifyFluidRenderPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyFluidRenderPower::getBlockCondition),
            FluidCondition.optionalCodec("fluid_condition").forGetter(ModifyFluidRenderPower::getFluidCondition),
            FluidState.CODEC.fieldOf("fluid").forGetter(ModifyFluidRenderPower::getFluid)
    ).apply(i, ModifyFluidRenderPower::new));
    private final BlockCondition blockCondition;
    private final FluidCondition fluidCondition;
    private final FluidState fluid;

    public ModifyFluidRenderPower(BaseSettings settings, BlockCondition blockCondition, FluidCondition fluidCondition, FluidState fluid) {
        super(settings);
        this.blockCondition = blockCondition;
        this.fluidCondition = fluidCondition;
        this.fluid = fluid;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public FluidCondition getFluidCondition() {
        return this.fluidCondition;
    }

    public FluidState getFluid() {
        return this.fluid;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    @Override
    public void grant(@NotNull Entity entity) {
        LevelRenderHelper.sendReloadPayload(entity);
    }

    @Override
    public void revoke(@NotNull Entity entity) {
        LevelRenderHelper.sendReloadPayload(entity);
    }
}
