package com.iafenvoy.origins.mixin.loot;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(NestedLootTable.class)
public abstract class NestedLootTableMixin {
    @SuppressWarnings("unchecked")
    @WrapOperation(method = "createItemStack", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Either;map(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/lang/Object;"))
    private <T, L extends ResourceKey<LootTable>, R extends LootTable> T replaceGetter(Either<L, R> either, Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction, Operation<T> original, Consumer<ItemStack> stackConsumer, LootContext lootContext) {
        ReloadableServerRegistries.Holder lookup = lootContext.getLevel().getServer().reloadableRegistries();
        Function<? super L, ? extends T> newGetter = l -> (T) lookup.getLootTable(l);
        return original.call(either, newGetter, rightFunction);
    }
}
