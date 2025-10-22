package com.iafenvoy.origins.data.action;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface Action {
    ActionType type();

    void execute(LivingEntity living, Level level, RegistryAccess access);
}
