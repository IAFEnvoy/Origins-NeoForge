package com.iafenvoy.origins.data.badge;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface Badge {
    BadgeType type();

    void execute(LivingEntity living, Level level, RegistryAccess access);
}
