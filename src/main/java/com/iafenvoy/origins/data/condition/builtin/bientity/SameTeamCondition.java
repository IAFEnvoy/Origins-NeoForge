package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/** Checks that both entities are members of the same non-null scoreboard team. */
public enum SameTeamCondition implements BiEntityCondition {
    INSTANCE;

    public static final MapCodec<SameTeamCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return source.getTeam() != null && source.isAlliedTo(target);
    }
}
