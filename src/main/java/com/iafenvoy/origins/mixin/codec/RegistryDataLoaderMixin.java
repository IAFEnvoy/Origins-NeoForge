package com.iafenvoy.origins.mixin.codec;

import com.google.gson.JsonElement;
import com.iafenvoy.origins.accessor.ResourceLoadingOps;
import com.mojang.serialization.Decoder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Inject(method = "loadElementFromResource", at = @At("HEAD"))
    private static <E> void beforeLoadElement(WritableRegistry<E> registry, Decoder<E> codec, RegistryOps<JsonElement> ops, ResourceKey<E> resourceKey, Resource resource, RegistrationInfo registrationInfo, CallbackInfo ci) {
        ((ResourceLoadingOps) ops).origins$setKey(resourceKey);
    }

    @Inject(method = "loadElementFromResource", at = @At("RETURN"))
    private static <E> void afterLoadElement(WritableRegistry<E> registry, Decoder<E> codec, RegistryOps<JsonElement> ops, ResourceKey<E> resourceKey, Resource resource, RegistrationInfo registrationInfo, CallbackInfo ci) {
        ((ResourceLoadingOps) ops).origins$setKey(null);
    }
}
