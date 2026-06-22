package com.iafenvoy.origins.accessor;

import net.minecraft.util.context.ContextKeySet;

public interface LootContextTypeHolder {
    ContextKeySet origins$getType();

    void origins$setType(ContextKeySet type);
}
