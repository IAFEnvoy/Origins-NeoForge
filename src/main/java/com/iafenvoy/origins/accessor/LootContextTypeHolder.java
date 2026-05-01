package com.iafenvoy.origins.accessor;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public interface LootContextTypeHolder {
    LootContextParamSet origins$getType();

    void origins$setType(LootContextParamSet type);
}
