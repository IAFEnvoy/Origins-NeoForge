package com.iafenvoy.origins.data.power;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface Power {
    PowerType type();

    void execute(LivingEntity living, Level level, RegistryAccess access);
}
