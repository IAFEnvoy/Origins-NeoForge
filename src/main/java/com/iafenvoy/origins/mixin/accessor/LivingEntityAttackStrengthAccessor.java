package com.iafenvoy.origins.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAttackStrengthAccessor {
    @Accessor("attackStrengthTicker")
    int origins$getAttackStrengthTicker();

    @Accessor("attackStrengthTicker")
    void origins$setAttackStrengthTicker(int value);
}
