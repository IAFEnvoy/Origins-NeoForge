package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum AttackTargetCondition implements BiEntityCondition {
    INSTANCE;
    public static final MapCodec<AttackTargetCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return source instanceof Mob mob && Objects.equals(target, mob.getTarget()) || source instanceof NeutralMob n && Objects.equals(target, n.getTarget());
    }
}
