package com.iafenvoy.origins.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class RegistryCodecs {
    public static <T> Codec<T> referenceOrDirect(Codec<Holder<T>> referenceCodec, Codec<T> directCodec) {
        return Codec.either(referenceCodec, directCodec).xmap(x -> x.map(Holder::value, Function.identity()), Either::right);
    }

    public static <T> Codec<Either<Holder<T>, TagKey<T>>> holderOrTag(ResourceKey<Registry<T>> key) {
        return Codec.either(RegistryFixedCodec.create(key), TagKey.hashedCodec(key));
    }

    public static <T> List<Holder<T>> listAll(List<Either<Holder<T>, TagKey<T>>> eitherList, RegistryAccess access, ResourceKey<Registry<T>> registry) {
        return listAll(eitherList, access.registryOrThrow(registry));
    }

    public static <T> List<Holder<T>> listAll(List<Either<Holder<T>, TagKey<T>>> eitherList, Registry<T> registry) {
        return eitherList.stream().flatMap(x -> x.map(Stream::of, y -> registry.getOrCreateTag(y).stream())).toList();
    }
}
