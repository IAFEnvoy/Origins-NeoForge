package com.iafenvoy.origins.accessor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;

public interface KeyableLootTable {
    ResourceKey<LootTable> getKey();

    void setup(ResourceKey<LootTable> lootTableKey, ReloadableServerRegistries.Holder lookup);
}
