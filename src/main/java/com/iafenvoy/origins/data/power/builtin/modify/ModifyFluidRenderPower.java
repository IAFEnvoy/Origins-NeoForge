package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.FluidCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ModifyFluidRenderPower(BlockCondition blockCondition, FluidCondition fluidCondition, Optional<FluidState> fluid) implements Power {

    public static final MapCodec<ModifyFluidRenderPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyFluidRenderPower::blockCondition),
            FluidCondition.optionalCodec("fluid_condition").forGetter(ModifyFluidRenderPower::fluidCondition),
            FluidState.CODEC.optionalFieldOf("fluid").forGetter( p -> p.fluid)
    ).apply(i, ModifyFluidRenderPower::new));
    
    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    public boolean test(Level world, BlockPos pos, FluidState fluid) {
        return blockCondition().test( world, pos) && fluidCondition().test(fluid);
    }

    @Override
    public void grant(@NotNull Entity entity) {
        // TODO
//        ApoliClient.shouldReloadWorldRenderer = true;
    }

    @Override
    public void revoke(@NotNull Entity entity) {
        // TODO
//        ApoliClient.shouldReloadWorldRenderer = true;
    }

}
