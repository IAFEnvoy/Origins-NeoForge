package com.iafenvoy.origins.util;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootHelper {
    public static final LootContextParamSet ALL = LootContextParamSet.builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.LAST_DAMAGE_PLAYER)
            .required(LootContextParams.DAMAGE_SOURCE)
            .required(LootContextParams.ATTACKING_ENTITY)
            .required(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.BLOCK_STATE)
            .required(LootContextParams.BLOCK_ENTITY)
            .required(LootContextParams.TOOL)
            .required(LootContextParams.EXPLOSION_RADIUS)
            .build();
}
