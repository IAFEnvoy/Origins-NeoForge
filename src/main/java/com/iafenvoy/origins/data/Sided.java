package com.iafenvoy.origins.data;

import javax.annotation.Nullable;

import net.neoforged.api.distmarker.Dist;

//修复::需要更好的端逻辑
public interface Sided {
    @Nullable
    default Dist side() {
        return null;
    }

    default boolean client() {
        return this.side() == null || this.side() == Dist.CLIENT;
    }

    default boolean server() {
        return this.side() == null || this.side() == Dist.DEDICATED_SERVER;
    }
}
