package com.iafenvoy.origins.util;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MiscUtil {
    public static Vec3 getPoseDependentEyePos(Entity entity) {
        return new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static InteractionResult reduce(InteractionResult first, InteractionResult second) {
        return second.consumesAction() && !first.consumesAction() || second.shouldSwing() && !first.shouldSwing() ? second : first;
    }
}
