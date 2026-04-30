package com.iafenvoy.origins.accessor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ReplacingLootContext extends LootContextTypeHolder {
    void apoli$setReplaced(ResourceKey<LootTable> key);

    boolean apoli$isReplaced(ResourceKey<LootTable> key);
}
