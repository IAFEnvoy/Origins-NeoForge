package com.iafenvoy.origins.mixin.loot;

import com.iafenvoy.origins.accessor.LootContextTypeHolder;
import com.iafenvoy.origins.accessor.ReplacingLootContext;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(LootContext.class)
public abstract class LootContextMixin implements ReplacingLootContext {
    @Shadow
    @Final
    private LootParams params;
    @Unique
    private final Set<ResourceKey<LootTable>> origins$replacedTables = new ObjectOpenHashSet<>();

    @Override
    public LootContextParamSet origins$getType() {
        return ((LootContextTypeHolder) this.params).origins$getType();
    }

    @Override
    public boolean origins$isReplaced(ResourceKey<LootTable> key) {
        return this.origins$replacedTables.contains(key);
    }

    @Override
    public void origins$setReplaced(ResourceKey<LootTable> key) {
        this.origins$replacedTables.add(key);
    }
}
