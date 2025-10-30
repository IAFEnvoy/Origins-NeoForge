package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.MiscUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public enum ExposedToSkyCondition implements EntityCondition {
    INSTANCE;
    public static final MapCodec<ExposedToSkyCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        Level world = entity.level();
        return world.canSeeSky(BlockPos.containing(MiscUtil.getPoseDependentEyePos(entity))) || world.canSeeSky(entity.blockPosition());
    }
}
