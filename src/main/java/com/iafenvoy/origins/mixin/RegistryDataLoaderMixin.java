package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.event.internal.ElementPostRegisterEvent;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Unique
    private static boolean origins$LOCAL;

    //Post for post-processor in Power
    @Inject(method = "lambda$loadElementFromResource$13", at = @At("RETURN"))
    private static <E> void postRegister(WritableRegistry<E> registry, ResourceKey<E> resourceKey, RegistrationInfo registrationInfo, E e, CallbackInfo ci) {
        if (origins$LOCAL) NeoForge.EVENT_BUS.post(new ElementPostRegisterEvent<>(registry, resourceKey, e));
        origins$LOCAL = false;
    }

    @Inject(method = "loadContentsFromManager", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryDataLoader;loadElementFromResource(Lnet/minecraft/core/WritableRegistry;Lcom/mojang/serialization/Decoder;Lnet/minecraft/resources/RegistryOps;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/server/packs/resources/Resource;Lnet/minecraft/core/RegistrationInfo;)V"))
    private static void preventNetworkCall(CallbackInfo ci) {
        origins$LOCAL = true;
    }
}
