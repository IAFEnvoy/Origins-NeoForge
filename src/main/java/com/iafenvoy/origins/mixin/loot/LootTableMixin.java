package com.iafenvoy.origins.mixin.loot;

import com.iafenvoy.origins.accessor.KeyableLootTable;
import com.iafenvoy.origins.accessor.ReplacingLootContext;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ReplaceLootTablePower;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements KeyableLootTable {
    @Unique
    private ResourceKey<LootTable> origins$key;
    @Unique
    private ReloadableServerRegistries.Holder origins$lookup;

    @Override
    public ResourceKey<LootTable> origins$getKey() {
        return this.origins$key;
    }

    @Override
    public void origins$setup(ResourceKey<LootTable> lootTableKey, ReloadableServerRegistries.Holder lookup) {
        this.origins$key = lootTableKey;
        this.origins$lookup = lookup;
    }

    @Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void replaceTable(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        if (!(context instanceof ReplacingLootContext replacingContext)) return;
        ContextKeySet contextType = replacingContext.origins$getType();
        ResourceKey<LootTable> key = this.origins$key;

        if (key == null || replacingContext.origins$isReplaced(key) || !context.hasParameter(LootContextParams.THIS_ENTITY))
            return;

        Entity thisEntity = context.getParameter(LootContextParams.THIS_ENTITY);
        Entity holder = thisEntity;

        if (contextType == LootContextParamSets.FISHING) {
            if (thisEntity instanceof FishingHook bobber) holder = bobber.getOwner();
        } else if (contextType == LootContextParamSets.ENTITY) {
            if (context.hasParameter(LootContextParams.ATTACKING_ENTITY))
                holder = context.getParameter(LootContextParams.ATTACKING_ENTITY);
        } else if (contextType == LootContextParamSets.PIGLIN_BARTER)
            if (thisEntity instanceof Piglin piglin)
                holder = piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
        if (holder == null) return;

        ReplaceLootTablePower.push((LootTable) (Object) this);

        Entity finalHolder = holder;
        Optional<LootTable> replacementTable = OriginDataHolder.get(holder).streamActivePowers(ReplaceLootTablePower.class)
                .filter(power -> power.hasReplacement(key) && power.doesApply(finalHolder, context))
                .map(power -> power.getReplacement(key)
                        .map(id -> this.origins$lookup.getLootTable(id))
                        .filter(table -> !LootTable.EMPTY.equals(table))
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        if (replacementTable.isEmpty()) return;

        LootTable table = replacementTable.get();
        replacingContext.origins$setReplaced(key);

        table.getRandomItemsRaw(context, lootConsumer);
        ci.cancel();
    }

    @WrapMethod(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
    private void wrapGenerateForReplacing(LootContext context, Consumer<ItemStack> lootConsumer, Operation<Void> original) {
        try {
            original.call(context, lootConsumer);
        } finally {
            ReplaceLootTablePower.clear();
        }
    }

    @Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;pushVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)Z"))
    private void popReplaced(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        ReplaceLootTablePower.pop();
    }

    @Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;popVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)V"))
    private void restoreReplaced(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        ReplaceLootTablePower.restore();
    }
}
