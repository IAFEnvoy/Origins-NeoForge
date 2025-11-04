package com.iafenvoy.origins.event.common;

import com.iafenvoy.origins.event.EntityResultedEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;

public class CanStandOnFluidEvent extends EntityResultedEvent<LivingEntity> {
    private final FluidState fluid;

    public CanStandOnFluidEvent(LivingEntity entity, FluidState fluid) {
        super(Result.DENY, entity);
        this.fluid = fluid;
    }

    public FluidState getFluid() {
        return this.fluid;
    }
}
