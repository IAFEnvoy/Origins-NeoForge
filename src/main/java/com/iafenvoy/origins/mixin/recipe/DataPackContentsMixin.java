package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.data.power.builtin.regular.RecipePower;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public abstract class DataPackContentsMixin {

    @Inject(method = "updateRegistryTags()V", at = @At("HEAD"))
    private void onRefresh(CallbackInfo ci) {
        RecipePower.registerPowerRecipes((ReloadableServerResources) (Object) this);
    }

}
