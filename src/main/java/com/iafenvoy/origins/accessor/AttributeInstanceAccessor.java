package com.iafenvoy.origins.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface AttributeInstanceAccessor {
    @Nullable LivingEntity origins$getEntity();

    void origins$setEntity(LivingEntity entity);
}
