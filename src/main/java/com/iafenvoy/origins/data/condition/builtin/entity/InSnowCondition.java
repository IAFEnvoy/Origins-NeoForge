package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.LevelUtil;
import com.iafenvoy.origins.util.MiscUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public enum InSnowCondition implements EntityCondition {
    INSTANCE;
    public static final MapCodec<InSnowCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return LevelUtil.inSnow(entity.level(), BlockPos.containing(MiscUtil.getPoseDependentEyePos(entity)), entity.blockPosition());
    }
}
