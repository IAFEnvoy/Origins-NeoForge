package com.iafenvoy.origins.mixin.loot;

import com.iafenvoy.origins.accessor.LootContextTypeHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootParams.Builder.class)
public abstract class LootParams$BuilderMixin {
    @ModifyReturnValue(method = "create", at = @At("RETURN"))
    private LootParams cacheType(LootParams original, LootContextParamSet type) {
        ((LootContextTypeHolder) original).origins$setType(type);
        return original;
    }
}
