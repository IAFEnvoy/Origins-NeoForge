package com.iafenvoy.origins.mixin.codec;

import com.google.gson.JsonElement;
import com.iafenvoy.origins.accessor.ResourceLoadingOps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Decoder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.resources.RegistryLoadTask$PendingRegistration")
public class RegistryLoadTask$PendingRegistrationMixin {
    @Inject(method = "loadFromResource", at = @At("HEAD"))
    private static <E> void origins$beforeLoad(Decoder<E> decoder, RegistryOps<JsonElement> ops, ResourceKey<E> key,
                                                Resource resource, CallbackInfoReturnable<Either<E, Exception>> cir) {
        ((ResourceLoadingOps) ops).origins$setKey(key);
    }

    @Inject(method = "loadFromResource", at = @At("RETURN"))
    private static <E> void origins$afterLoad(Decoder<E> decoder, RegistryOps<JsonElement> ops, ResourceKey<E> key,
                                               Resource resource, CallbackInfoReturnable<Either<E, Exception>> cir) {
        ((ResourceLoadingOps) ops).origins$setKey(null);
    }
}
