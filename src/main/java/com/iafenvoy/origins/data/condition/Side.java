package com.iafenvoy.origins.data.condition;

import javax.annotation.Nullable;

import net.neoforged.api.distmarker.Dist;

public interface Side {

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
