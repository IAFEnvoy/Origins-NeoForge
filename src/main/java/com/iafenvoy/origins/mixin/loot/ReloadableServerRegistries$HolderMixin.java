package com.iafenvoy.origins.mixin.loot;

import com.iafenvoy.origins.accessor.KeyableLootTable;
import com.iafenvoy.origins.data.power.builtin.regular.ReplaceLootTablePower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerRegistries.Holder.class)
public abstract class ReloadableServerRegistries$HolderMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setupLootTables(HolderLookup.Provider registryManager, CallbackInfo ci) {
        registryManager.lookupOrThrow(Registries.LOOT_TABLE).listElements().forEach(reference -> {
            ResourceKey<LootTable> key = reference.getKey();
            if (reference.value() instanceof KeyableLootTable keyable)
                keyable.origins$setup(key, (ReloadableServerRegistries.Holder) (Object) this);
        });
    }

    @ModifyReturnValue(method = "getLootTable", at = @At("RETURN"))
    private LootTable getReplacedOrNormalTable(LootTable original, ResourceKey<LootTable> key) {
        if (key.equals(ReplaceLootTablePower.REPLACED_TABLE_KEY)) return ReplaceLootTablePower.peek();
        else return original;
    }
}
