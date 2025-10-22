package com.iafenvoy.origins.data.condition;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface Condition {
    ConditionType type();

    void execute(LivingEntity living, Level level, RegistryAccess access);
}
