package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.KeyableLootTable;
import com.iafenvoy.origins.accessor.LootContextTypeHolder;
import com.iafenvoy.origins.accessor.ReplacingLootContext;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ReplaceLootTablePower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

//FIXME::Split
public class ReplaceLootTablePowerTypeMixin {
    @Mixin(ReloadableServerRegistries.Holder.class)
    public static abstract class Replacer {
        @Inject(method = "<init>", at = @At("TAIL"))
        private void setupLootTables(RegistryAccess.Frozen registryManager, CallbackInfo ci) {
            registryManager.registryOrThrow(Registries.LOOT_TABLE).holders().forEach(reference -> {

                ResourceKey<LootTable> key = reference.getKey();

                if (reference.value() instanceof KeyableLootTable keyable) {
                    keyable.setup(key, (ReloadableServerRegistries.Holder) (Object) this);
                }

            });
        }

        @ModifyReturnValue(method = "getLootTable", at = @At("RETURN"))
        private LootTable getReplacedOrNormalTable(LootTable original, ResourceKey<LootTable> key) {

            if (key.equals(ReplaceLootTablePower.REPLACED_TABLE_KEY)) {
                return ReplaceLootTablePower.peek();
            } else {
                return original;
            }

        }

    }

    @Mixin(NestedLootTable.class)
    public static abstract class NestedReplacer {

        @SuppressWarnings("unchecked")
        @WrapOperation(method = "createItemStack", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Either;map(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/lang/Object;"))
        private <T, L extends ResourceKey<LootTable>, R extends LootTable> T replaceGetter(Either<L, R> either, Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction, Operation<T> original, Consumer<ItemStack> stackConsumer, LootContext lootContext) {

            ReloadableServerRegistries.Holder lookup = lootContext.getLevel().getServer().reloadableRegistries();
            Function<? super L, ? extends T> newGetter = l -> (T) lookup.getLootTable(l);

            return original.call(either, newGetter, rightFunction);

        }

    }

    @Mixin(LootTable.class)
    public static abstract class LootTableCache implements KeyableLootTable {

        @Unique
        private ResourceKey<LootTable> apoli$key;

        @Unique
        private ReloadableServerRegistries.Holder apoli$lookup;

        @Override
        public ResourceKey<LootTable> getKey() {
            return this.apoli$key;
        }

        @Override
        public void setup(ResourceKey<LootTable> lootTableKey, ReloadableServerRegistries.Holder lookup) {
            this.apoli$key = lootTableKey;
            this.apoli$lookup = lookup;
        }

        @Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
        private void replaceTable(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {

            if (!(context instanceof ReplacingLootContext replacingContext)) {
                return;
            }

            LootContextParamSet contextType = replacingContext.apoli$getType();
            ResourceKey<LootTable> key = this.apoli$key;

            if (key == null || replacingContext.apoli$isReplaced(key)) {
                return;
            }

            Entity thisEntity = context.getParam(LootContextParams.THIS_ENTITY);
            Entity holder = thisEntity;

            if (contextType == LootContextParamSets.FISHING) {

                if (thisEntity instanceof FishingHook bobber) {
                    holder = bobber.getOwner();
                }

            } else if (contextType == LootContextParamSets.ENTITY) {

                if (context.hasParam(LootContextParams.ATTACKING_ENTITY)) {
                    holder = context.getParam(LootContextParams.ATTACKING_ENTITY);
                }

            } else if (contextType == LootContextParamSets.PIGLIN_BARTER) {

                if (thisEntity instanceof Piglin piglin) {
                    holder = piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
                }

            }


            ReplaceLootTablePower.push((LootTable) (Object) this);

            Entity finalHolder = holder;
            Optional<LootTable> replacementTable = OriginDataHolder.get(holder).streamActivePowers(ReplaceLootTablePower.class)
                    .filter(power -> power.hasReplacement(key) && power.doesApply(finalHolder, context))
                    .map(power -> power.getReplacement(key)
                            .map(id -> this.apoli$lookup.getLootTable(id))
                            .filter(table -> !LootTable.EMPTY.equals(table))
                    )
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            if (replacementTable.isEmpty()) {
                return;
            }

            LootTable table = replacementTable.get();
            replacingContext.apoli$setReplaced(key);

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

    @Mixin(LootContext.class)
    public static abstract class LootContextCache implements ReplacingLootContext {
        @Shadow
        @Final
        private LootParams params;
        @Unique
        private final Set<ResourceKey<LootTable>> apoli$replacedTables = new ObjectOpenHashSet<>();

        @Override
        public LootContextParamSet apoli$getType() {
            return ((LootContextTypeHolder) this.params).apoli$getType();
        }

        @Override
        public boolean apoli$isReplaced(ResourceKey<LootTable> key) {
            return this.apoli$replacedTables.contains(key);
        }

        @Override
        public void apoli$setReplaced(ResourceKey<LootTable> key) {
            this.apoli$replacedTables.add(key);
        }

    }

    @Mixin(LootParams.class)
    public static abstract class LootContextParametersCache implements LootContextTypeHolder {

        @Unique
        private LootContextParamSet apoli$contextType;

        @Override
        public LootContextParamSet apoli$getType() {
            return Objects.requireNonNull(this.apoli$contextType, "Loot context parameters are not initialized properly!");
        }

        @Override
        public void apoli$setType(LootContextParamSet type) {
            this.apoli$contextType = type;
        }

    }

    @Mixin(LootParams.Builder.class)
    public static abstract class LootContextParametersCacheInit {
        @ModifyReturnValue(method = "create", at = @At("RETURN"))
        private LootParams cacheType(LootParams original, LootContextParamSet type) {

            ((LootContextTypeHolder) original).apoli$setType(type);

            return original;

        }

    }
}
