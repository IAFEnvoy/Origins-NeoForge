package com.iafenvoy.origins.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("increaseAirSupply")
    int origins$callIncreaseAirSupply(int air);

    @Invoker("decreaseAirSupply")
    int origins$callDecreaseAirSupply(int air);
}
